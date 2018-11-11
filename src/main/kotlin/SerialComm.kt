package sim868.kotlin

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import io.reactivex.Observable
import java.io.PrintWriter
import java.util.*


class SerialComm(portDescription: String, private var baudRate: Int = 115200) {

    private var serialPort: SerialPort
    private var portDescription: String? = portDescription
    private var status = false

    init {
        serialPort = SerialPort.getCommPort(this.portDescription).also { it.baudRate = baudRate }
        this.status = openConnection()
    }


    fun openConnection(): Boolean {

        return if (serialPort.openPort()) {
            return try { Thread.sleep(100)
                true
            } catch (e: Exception) {
                throw  Exception(e)
            }
        } else {
            false
        }
    }

    fun serialRead(): Observable<String> {

        return Observable.create<String> { emitter ->
            serialPort.addDataListener(object : SerialPortDataListener {

                override fun getListeningEvents(): Int {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE
                }

                override fun serialEvent(serialPortEvent: SerialPortEvent) {

                    if (serialPortEvent.eventType != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                        println("event_type=" + serialPortEvent.eventType)
                    }

                    try {
                        val len = serialPortEvent.serialPort.bytesAvailable()
                        val data = ByteArray(len)
                        serialPortEvent.serialPort.readBytes(data, len.toLong())
                        emitter.onNext(String(data))
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
            if (!serialPort.openPort()) {
                serialPort.closePort()
            }
        }
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
}