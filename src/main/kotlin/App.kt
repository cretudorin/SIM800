package sim868.kotlin

import com.fazecast.jSerialComm.SerialPort
import com.pi4j.io.gpio.RaspiPin
import com.pi4j.platform.PlatformManager
import io.jenetics.jpx.GPX
import java.io.File
import java.io.FilenameFilter
import java.net.URI
import jdk.nashorn.tools.ShellFunctions.input
import com.pi4j.system.SystemInfo

//sudo systemctl stop serial-getty@ttyAMA0.service
// sudo systemctl disable serial-getty@ttyS0.service


fun turnOnModem() {
    Thread.sleep(200)
    val gpio = Gpio(RaspiPin.GPIO_07)
    gpio.setLow()
    Thread.sleep(4000)
    gpio.setHigh()
    Thread.sleep(4000)
    gpio.release()
}


//
//    fun startGPRS() {
//        simHat.setApn("web.vodafone.de\",\"\",\"")
//        simHat.checkGPRSAttachment()
//        simHat.bringUpWireless()
//        simHat.sendCommand("+CIFSR")
//        simHat.enableGPRSData()
//        simHat.checkLocalIp()
//    }
//
//
//    fun httpGet(url: String, port: String) {
//
//
//        simHat.sendCommand("+SAPBR=3,1,\"APN\",\"web.vodafone.de\"")
//        simHat.sendCommand("+SAPBR=1,1")
//        simHat.sendCommand("+HTTPINIT")
//        simHat.sendCommand("+HTTPPAR=\"CID\",1")
//        simHat.sendCommand("+HTTPPARA=\"URL\",\"http://$url:$port\"")
//        simHat.sendCommand("+HTTPACTION=0")

//        Thread.sleep(5000)
//        simHat.sendCommand("+HTTPREAD")
//
//        simHat.once("+HTTPREAD:") {
//            simHat.sendCommand("+HTTPTERM")
//        }
//    }

fun getPort(): String {

    val os = System.getProperty("os.name")

    if (os == "Linux") {
        File("/dev").walkTopDown().forEach {

            val filename = it.toString()
            if (
                filename == "/dev/ttyS0" ||
                filename == "/dev/ttyAMA0" ||
                filename == "/dev/ttyUSB0" ||
                filename == "/dev/ttyUSB1"
            ) {
                return filename
            }
        }
    }

    return ""
}


fun main(args: Array<String>) {
//    val webSocket = WebSocket(URI("ws://185.122.87.86:8080"))
//    File("/dev").walkTopDown().forEach {
//        println(it)
//    }
//    turnOnModem()
    val port = getPort()
    println("Sim init")
    println(port)
    val simHat =  Sim868(port)

    simHat.executeCommand(SIM868Commands.echoOff)
    simHat.writeCommand(SIM868Commands.gpsState, "1")
    simHat.writeCommand(SIM868Commands.getPositionOnInterval, "2")


//    val file = File("file.gpx")
    simHat.addEventListener(SIM868Responses.gpsInfo) {
        val gpsData = GpsData(it)

//        gpsData.fixStatus
//        println(gpsData.fixStatus)
//        println(gpsData.lat)
//        println(gpsData.long)
    }


//    val gpx = GPX.builder()
//        .addTrack({ track ->
//            track
//                .addSegment({ segment ->
//                    segment
//                        .addPoint({ p -> p.lat(48.2081743).lon(16.3738189).ele(160) })
//                        .addPoint({ p -> p.lat(48.2081743).lon(16.3738189).ele(161) })
//                        .addPoint({ p -> p.lat(48.2081743).lon(16.3738189).ele(162) })
//                })
//        })
//        .build()
//
//    simHat.serialObservable.subscribe({
//        println(it)
//    }, { throw(it) }, {
//        println("connection completed")
//    })


//    startGPRS()
//    httpGet("zmeurica.ddns.net", "8080")

}


