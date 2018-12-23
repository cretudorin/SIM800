package sim800.kotlin

import java.io.File

//sudo systemctl stop serial-getty@ttyAMA0.service
//sudo systemctl disable serial-getty@ttyS0.service

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
    val simHat = Sim800(port, apn="web.vodafone.de")

    // listen to all
    simHat.serialObservable.subscribe({
        println(it)
    }, { throw(it) }, {
        println("connection completed")
    })

    simHat.executeCommand(Sim800Commands.echoOn)


    // get GPS data
    simHat.writeCommand(Sim800Commands.gpsState, "1")
    simHat.writeCommand(Sim800Commands.getPositionOnInterval, "2")
//    var i = 0
//    simHat.addEventListener(SIM800Responses.gpsInfo) {
//
//        val gpsData = DataParsers.parseGps(it)
//
//        gpsData?.let {
//            i++
//            println(gpsData.lat)
//        }
//
//        if (i == 10) {
//            simHat.disposeEventListener(SIM800Responses.gpsInfo)
//        }
//    }

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

//    simHat.executeCommand(Sim800Commands.dial("+")
//    simHat.writeCommand(Sim800Commands.smsMessageFormat, "1")
//    simHat.executeCommand(Sim800Commands.allSMS)


//    simHat.enableGprs()
//    simHat.httpGet("zmeurica.ddns.net", "8080")

}


