package sim800.kotlin

import com.pi4j.io.gpio.RaspiPin
import io.reactivex.Observable
import io.reactivex.disposables.Disposable


class Sim800(port: String, baud_rate: Int = 115200, var apn: String) {

    private var serialPort: SerialComm = SerialComm(port, baud_rate)
    val serialObservable = serialPort.serialRead()

    private val eventListeners = mutableMapOf<String, Disposable?>()

    fun once(event: String, eventHandler: (data: String) -> Unit) {

        eventListeners[event] = serialObservable.takeUntil {
            it.toUpperCase().contains(event.toUpperCase())
        }.subscribe {
            if (it.toUpperCase().contains(event.toUpperCase())) {
                eventHandler(it)
            }
        }
    }

    fun addEventListener(event: String, eventHandler: (data: String) -> Unit) {
        eventListeners[event] = serialObservable.subscribe {
            if (it.toUpperCase().contains(event.toUpperCase())) {
                eventHandler(it)
            }
        }
    }

    fun disposeEventListener(event: String) = eventListeners[event]?.dispose()

    private fun sendCommand(command: String) = Thread.sleep(100).also { serialPort.serialWrite("AT$command\r") }

    fun testCommand(command: String) = sendCommand("$command=?")
    fun readCommand(command: String) = sendCommand("$command?")
    fun writeCommand(command: String, value: String) = sendCommand("$command=$value")
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

    fun enableGprs() {

        writeCommand(Sim800Commands.setBearer, """3,1,"Contype","GPRS"""")
        writeCommand(Sim800Commands.setBearer, """3,1,"APN",$apn""")
        writeCommand(Sim800Commands.setBearer, "1,1")
    }

    fun disableGprs() {
        writeCommand(Sim800Commands.setBearer, "0,1")
    }

    fun httpGet(url: String, port: String): Observable<String> {

        executeCommand(Sim800Commands.initHttpService)
        writeCommand(Sim800Commands.setHttpParam, """"CID",1""")
        writeCommand(Sim800Commands.setHttpParam, """"URL","$url:$port"""")
        executeCommand(Sim800Commands.doGetRequest)

        val result = "AT+HTTPREAD+HTTPREAD:12HelloWorld!OK"
        once(SIM800Responses.httpResponse) {

        }

        once("+HTTPACTION:") {
            executeCommand(Sim800Commands.httpRead)
            executeCommand(Sim800Commands.stopHttpService)
        }

        return Observable.create<String> { it.onComplete() }.publish().autoConnect()
    }


}

