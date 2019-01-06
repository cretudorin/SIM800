package sim800.kotlin

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
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
                filename == "/dev/ttyUSB1"
            ) {
                return filename
            }
        }
    }

    return ""
}

class Main : CliktCommand() {

    private val gps: Int? by option(help = "Get GPS position [interval]").int()

    private val port = getPort()
    private val simHat = Sim800(port, apn = "web.vodafone.de")

    override fun run() {

        gps?.let {
            simHat.getGPS(it).subscribe { gpsData ->
                println(gpsData.toString())
            }
        }
    }
}

fun main(args: Array<String>) = Main().main(args)
