package sim800.kotlin

import com.pi4j.io.gpio.RaspiPin
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.time.LocalDateTime
import java.time.ZoneOffset


class Sim800(port: String, baud_rate: Int = 115200, var apn: String) {

    private var serialPort: SerialComm = SerialComm(port, baud_rate)
    private val eventListeners = mutableMapOf<String, Disposable?>()
    private var GPRSStatus = false

    val serialObservable = serialPort.serialRead()

    fun once(event: String, eventHandler: (data: String, eventId: String) -> Unit) {

        val randomInt = (0..LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)).random()
        val eventId = "once_${event}_$randomInt"
        eventListeners[eventId] = serialObservable.takeUntil {
            it.toUpperCase().contains(event.toUpperCase())
        }.subscribe {
            if (it.toUpperCase().contains(event.toUpperCase())) {
                eventHandler(it, eventId)
                disposeListener(eventId)
            }
        }
    }


    fun addEventListener(event: String, eventHandler: (data: String, eventId: String) -> Unit) {

        val randomInt = (0..LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)).random()
        val eventId = "${event}_$randomInt"

        eventListeners[eventId] = serialObservable.subscribe {
            if (it.toUpperCase().contains(event.toUpperCase())) {
                eventHandler(it, eventId)
            }
        }
    }

    fun disposeListener(eventId: String) = eventListeners[eventId]?.dispose()


    private fun sendCommand(command: String) = Thread.sleep(100).also { serialPort.serialWrite("AT$command\r") }

    fun testCommand(command: String) = sendCommand("$command=?")
    fun readCommand(command: String) = sendCommand("$command?")
    fun writeCommand(command: String, value: String) = sendCommand("$command=$value")
    fun writeCommand(command: String, value: Int) = sendCommand("$command=$value")

    fun executeCommand(command: String) = sendCommand(command)
    fun sendRawCommand(command: String) = Thread.sleep(100).also { serialPort.serialWrite("$command\r") }

    // only for Waveshare GSM/GPRS/GPS HAT when used as hat
    fun togglePowerState() {
        val gpio = Gpio(RaspiPin.GPIO_07)
        gpio.setLow()
        Thread.sleep(4000)
        gpio.setHigh()
        Thread.sleep(4000)
        gpio.release()
    }

    fun enableGPRS(callback: (result: Boolean) -> Unit) {

        fun enable() {

            addEventListener(SIM800Responses.setBearer) { response: String, eventId: String ->
                if (!response.contains("ERROR")) {

                    if (response == """AT+SAPBR=1,1""") {
                        callback(true)
                        disposeListener(eventId)
                        GPRSStatus = true
                    }

                } else {
                    callback(false)
                }

            }
            writeCommand(Sim800Commands.setBearer, """3,1,"Contype","GPRS"""")
            writeCommand(Sim800Commands.setBearer, """3,1,"APN",$apn""")
            writeCommand(Sim800Commands.setBearer, "1,1")
        }

        if (!GPRSStatus) {
            enable()
        } else {
            callback(true)
        }
    }


    fun disableGPRS(callback: (result: Boolean) -> Unit) {

        fun disable() {
            once(SIM800Responses.setBearer) { response: String, _: String ->

                val result = !response.contains("ERROR")
                if (result) {
                    GPRSStatus = false
                }

                callback(result)
            }
            writeCommand(Sim800Commands.gprsAttachment, "0,1")
        }

        if (GPRSStatus) {
            disable()
        } else {
            callback(true)
        }
    }

    fun getGPS(interval: Int): Observable<GpsData> {

        writeCommand(Sim800Commands.gpsState, 1)
        writeCommand(Sim800Commands.getPositionOnInterval, interval)

        return Observable.create<GpsData> { emitter ->
            addEventListener(SIM800Responses.gpsInfo) { response: String, _: String ->

                val gpsData = DataParsers.parseGps(response)
                gpsData?.let { self ->
                    emitter.onNext(self)
                }
            }
        }.publish().autoConnect()

    }


    fun disableGPS() {
        writeCommand(Sim800Commands.gpsState, 0)
    }


    fun httpGet(url: String, callback: (result: String) -> Unit) {

        executeCommand(Sim800Commands.initHttpService)
        writeCommand(Sim800Commands.setHttpParam, """"CID",1""")
        writeCommand(Sim800Commands.setHttpParam, """"URL","$url"""")
        executeCommand(Sim800Commands.doGetRequest)

        once(SIM800Responses.httpResponse) { _: String, _: String ->
            executeCommand(Sim800Commands.httpRead)
        }

        once(SIM800Responses.httpRead) { response: String, _: String ->

            val bytesRegex = """(\d+)""".toRegex()
            val bytesString = bytesRegex.find(response)

            bytesString?.range?.last?.let {
                callback(response.subSequence(it + 1, response.length - 2).toString())
            }
        }

    }


}

