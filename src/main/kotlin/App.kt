package sim868.kotlin


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


    fun httpGet(url: String, port: String) {


        simHat.sendCommand("+SAPBR=3,1,\"APN\",\"web.vodafone.de\"")
        simHat.sendCommand("+SAPBR=1,1")
        simHat.sendCommand("+HTTPINIT")
        simHat.sendCommand("+HTTPPAR=\"CID\",1")
        simHat.sendCommand("+HTTPPARA=\"URL\",\"http://$url:$port\"")
        simHat.sendCommand("+HTTPACTION=0")
//
        Thread.sleep(5000)
        simHat.sendCommand("+HTTPREAD")

        simHat.serialObservable.takeUntil {it.toUpperCase().contains("+HTTPREAD:")}.subscribe { simHat.sendCommand("+HTTPTERM")}



//
    }

    simHat.serialObservable.subscribe({
        println("1: ${parseResponse(it)}")
    }, { throw(it) }, {
        println("connection completed")
    })

    simHat.setGpsStatus(false)
//    simHat.getPosition(2)
    startGPRS()
    httpGet("zmeurica.ddns.net", "8080")

}


