package sim868.kotlin

import com.pi4j.io.gpio.*
import java.net.URI
import com.fazecast.jSerialComm.*
import com.sun.deploy.util.SystemUtils.readBytes
import com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_AVAILABLE
import com.fazecast.jSerialComm.SerialPortEvent
import com.fazecast.jSerialComm.SerialPortDataListener
import com.sun.deploy.util.SystemUtils.readBytes
import com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_AVAILABLE
import com.fazecast.jSerialComm.SerialPort.getCommPorts
import com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_WRITTEN







fun main(args: Array<String>) {

//    val webSocket = WebSocket(URI("ws://185.122.87.86:8080"))

//    Thread.sleep(200)
//    val gpio = Gpio(RaspiPin.GPIO_07)
//    gpio.setLow()
//    Thread.sleep(4000)
//    gpio.setHigh()
//    Thread.sleep(4000)
//    gpio.release()

    val simHat = Sim868("/dev/ttyUSB0")
    fun pinResponse(response: String) {
        println(response)
    }

    fun newSms(response: String): String {
        return (response)
    }

    fun parseResponse(response: String): String {

        return when {
            (response.contains("+CMTI:")) -> newSms(response)
//            (response.contains("+CPIN:")) -> pinResponse(response)
//            (response.contains("+CLIP")) -> simHat.answerCall()
            (response.contains("+UGNSINF")) -> simHat.decodeGPS(response).toString()
            else -> return response
        }
    }

    fun startGPRS() {
        simHat.setApn("web.vodafone.de\",\"\",\"")
        simHat.checkGPRSAttachment()
        simHat.bringUpWireless()
        simHat.sendCommand("+CIFSR")
        simHat.enableGPRSData()
        simHat.checkLocalIp()
    }

    fun httpGet(url: String, port: String, callback: (result: String) -> Unit): Unit {
//
        simHat.getObservable().subscribe({
            if (it.contains("+HTTPACTION:")) {
                val length = it.split(",").toList().last().replace("\\s".toRegex(), "").toInt()
                println(length)
                println(simHat.serialPort.serialRead(length))
            }

        }, { throw(it) }, { println("connection completed") })
        Thread.sleep(300)
        startGPRS()

        simHat.sendCommand("+SAPBR=3,1,\"APN\",\"web.vodafone.de\"")
        simHat.sendCommand("+SAPBR=1,1")
        simHat.sendCommand("+HTTPINIT")
        simHat.sendCommand("+HTTPPAR=\"CID\",1")
        simHat.sendCommand("+HTTPPARA=\"URL\",\"http://$url:$port\"")
        simHat.sendCommand("+HTTPACTION=0")
        simHat.sendCommand("+HTTPREAD")
        Thread.sleep(5000)
        simHat.sendCommand("+HTTPTERM")


        callback("test")
    }

    simHat.getObservable().subscribe({
        println(parseResponse(it))

    }, { throw(it) }, {
        println("connection completed")
    })

    simHat.setGpsStatus(true)
    simHat.getPosition(2)


}


