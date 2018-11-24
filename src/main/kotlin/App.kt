package sim868.kotlin

import java.io.File

//sudo systemctl stop serial-getty@ttyAMA0.service
// sudo systemctl disable serial-getty@ttyS0.service




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

    val port = getPort()
    val simHat =  Sim800(port)

    simHat.executeCommand(Sim800Commands.echoOn)



    // get GPS data
    simHat.writeCommand(Sim800Commands.gpsState, "1")
    simHat.writeCommand(Sim800Commands.getPositionOnInterval, "2")
    var i = 0
    simHat.addEventListener(SIM868Responses.gpsInfo) {

        val gpsData = DataParsers.parseGps(it)

        gpsData?.let {
            i++
            println(gpsData.lat)
        }

        if(i == 10){
            simHat.disposeEventListener(SIM868Responses.gpsInfo)
        }
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

//    new sms
//    +CMTI:"SM",4
//    simHat.executeCommand(Sim800Commands.dial("+")
//    simHat.writeCommand(Sim800Commands.smsMessageFormat, "1")
//    simHat.executeCommand(Sim800Commands.allSMS)

    // listen to all
//    simHat.serialObservable.subscribe({
//        println(it)
//    }, { throw(it) }, {
//        println("connection completed")
//    })


//    startGPRS()
//    httpGet("zmeurica.ddns.net", "8080")

}


