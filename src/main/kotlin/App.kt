package sim800.kotlin

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
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
                filename == "/dev/ttyUSB1" ||
                filename == "/dev/ttyUSB2"
            ) {
                return filename
            }
        }
    }

    return ""
}

class Main : CliktCommand() {

    private val gps: Int? by option(help = "Get GPS position [interval]").int()
    private val gprs: Int? by option(help = "Set gprs status [ 1 / 0 ]").int()

    private val port = getPort()
    private val simHat = Sim800(port, apn = "web.vodafone.de")


    override fun run() {

        simHat.executeCommand(Sim800Commands.echoOn)

        simHat.serialObservable.subscribe { println(it) }

//        simHat.enableGPRS { result ->
//            println(result)
//        }



        gprs?.let {
            if (it == 1) {
                simHat.enableGPRS { result -> println(result) }
            } else {
                simHat.disableGPRS { result -> println(result) }
            }
        }

        simHat.httpGet("https://nc.gradinacufluturi.ro"){
            println(it)
        }

        gps?.let {
            simHat.getGPS(it).subscribe { gpsData ->
                println(gpsData.toJson())
            }
        }
    }
}


fun main(args: Array<String>) = Main().main(args)
