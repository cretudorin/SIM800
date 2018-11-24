package sim868.kotlin

import com.pi4j.io.gpio.RaspiPin
import io.reactivex.disposables.Disposable


class Sim800(port: String, baud_rate: Int = 115200) {

    var serialPort: SerialComm = SerialComm(port, baud_rate)
    val serialObservable = serialPort.serialRead()

    val eventListeners = mutableMapOf<String, Disposable?>()

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

    fun disposeEventListener(event: String){

        eventListeners[event]?.dispose()
    }

    private fun sendCommand(command: String) = Thread.sleep(50).also { serialPort.serialWrite("AT$command\r") }

    fun testCommand(command: String) {
        sendCommand("$command=?")
    }

    fun readCommand(command: String) {
        sendCommand("$command?")
    }

    fun writeCommand(command: String, value: String) {
        sendCommand("$command=$value")
    }

    fun executeCommand(command: String) {
        sendCommand(command)
    }

    fun sendRawCommand(command: String) = Thread.sleep(50).also { serialPort.serialWrite(command) }

    // only for Waveshare GSM/GPRS/GPS HAT when used as hat
    fun togglePowerState() {
        val gpio = Gpio(RaspiPin.GPIO_07)
        gpio.setLow()
        Thread.sleep(4000)
        gpio.setHigh()
        Thread.sleep(4000)
        gpio.release()
    }
}

