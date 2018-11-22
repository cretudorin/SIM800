package sim868.kotlin

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import io.reactivex.Observable
import java.io.PrintWriter
import java.util.*


class SerialComm(port: String, private var baudRate: Int = 115200) {

    private var serialPort = SerialPort.getCommPort(port).also {
        it.baudRate = baudRate
        it.openPort()
    }

    fun serialRead(): Observable<String> {

        return Observable.create<String> { emitter ->
            serialPort.addDataListener(object : SerialPortDataListener {

                override fun getListeningEvents(): Int = SerialPort.LISTENING_EVENT_DATA_AVAILABLE

                override fun serialEvent(serialPortEvent: SerialPortEvent) {

                    try {

                        if (serialPort.bytesAvailable() > 0) {
                            val readBuffer = ByteArray(serialPort.bytesAvailable())
                            serialPort.readBytes(readBuffer, readBuffer.size.toLong())

                            val result = String(readBuffer)
                            if (result.isNotBlank()) {
                                emitter.onNext(result.replace("\\s".toRegex(), ""))
                            }

                        }

                    } catch (exp: Exception) {
                        emitter.onError(exp)
                    }
                }
            })
        }
            .publish().autoConnect()
    }

    fun readString(): String {

        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1, 1)
        var out = ""
        val input = Scanner(serialPort.inputStream)
        try {
            while (input.hasNext())
                out += input.next() + "\n"
            input.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return out
    }

    fun serialWrite(s: String) {

        try {

            Thread.sleep(5)
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 1, 1)
            val pout = PrintWriter(serialPort.outputStream)
            pout.print(s)
            pout.flush()

        } catch (e: Exception) {
            throw Exception("Not connected")
        }
    }

    fun serialRead(limit: Int): String {

        //in case of unlimited incoming data, set a limit for number of readings
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1, 1)
        var out = ""
        var count = 0
        val input = Scanner(serialPort.inputStream)
        try {
            while (input.hasNext() && count <= limit) {
                out += input.next() + "\n"
                count++
            }
            input.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return out
    }
}