package sim868.kotlin

fun Boolean.toInt() = if (this) 1 else 0

class Sim868(port: String, baud_rate: Int = 115200) {

    var serialPort: SerialComm = SerialComm(port, baud_rate)
    val serialObservable = serialPort.serialRead()


    fun once(event: String, eventHandler: (data: String) -> Unit) {
        serialObservable.takeUntil {
            it.toUpperCase().contains(event.toUpperCase())
        }.subscribe {
            if (it.toUpperCase().contains(event.toUpperCase())) {
                eventHandler(it)
            }
        }
    }

    fun addEventListener(event: String, eventHandler: (data: String) -> Unit) {
        serialObservable.subscribe {
            if (it.toUpperCase().contains(event.toUpperCase())) {
                eventHandler(it)
            }
        }
    }

    private fun sendCommand(command: String) = Thread.sleep(100).also { serialPort.serialWrite("AT$command\r") }

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

    fun sendRawCommand(command: String) = Thread.sleep(100).also { serialPort.serialWrite(command) }

}

