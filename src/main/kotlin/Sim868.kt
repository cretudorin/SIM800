package sim868.kotlin

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject

fun Boolean.toInt() = if (this) 1 else 0

class Sim868(port: String, baud_rate: Int = 115200) {

    var serialPort: SerialComm = SerialComm(port, baud_rate)
    val serialObservable = serialPort.serialRead()


    // commands
    fun sendCommand(command: String) = Thread.sleep(100).also { serialPort.serialWrite("AT$command\r") }

    fun sendRawCommand(command: String) = Thread.sleep(100).also { serialPort.serialWrite(command) }
    fun repeatCommand() = sendCommand("A/")
    fun readString(): String = serialPort.readString()
    fun setEcho(value: Boolean) = sendCommand("E${value.toInt()}")

    // GPS
    fun decodeGPS(stringData: String): Map<String, String> {
        val gpsArray = stringData.replace("+UGNSINF", "").split(",")
        val mapping = listOf(
            "gpsRunStatus",
            "fixStatus",
            "utcDataTime",
            "lat",
            "long",
            "mslAltitude",
            "speedOverGround",
            "courseOverGround", // degrees
            "fixMode",
            "reserved1",
            "hdop", // Horizontal Dilution of Precision
            "pdop", // Position Dilution of Precision
            "vdop", // Vertical Dilution of Precision
            "reserved2",
            "gpsSatellitesInView",
            "gnssSatellitesUsed",
            "glonassStatelitesInView",
            "reserved3",
            "c/n0Max",
            "hpa", // Horizontal Position Accuracy
            "vpa" // Vertical Position Accuracy
        )
        return if (gpsArray.count() == 21) {
            gpsArray.associateBy({ mapping[gpsArray.indexOf(it)] }, { it })
        } else {
            hashMapOf("error" to "Gps no data")
        }
    }

    fun setGpsStatus(value: Boolean) = sendCommand("+CGNSPWR=${value.toInt()}")
    fun getLocation() = sendCommand("+CGNSINF")

    fun getPosition(interval: Int = 2): Observable<Map<String, String>> {

        setGpsStatus(true)
        sendCommand("+CGNSURC=$interval")

        val observable = Observable.create<Map<String, String>> { emitter ->

            serialObservable.subscribe { it ->
                if (it.contains("+UGNSINF")) {
                    emitter.onNext(decodeGPS(it))
                }
            }
        }
        observable.publish()
        return observable
    }

    // GSM
    fun gsmNetworkScan() = sendCommand("+COPS=?")

    fun gsmBand() = sendCommand("+CBAND?")
    fun ussdCommand(command: String) = sendCommand("+CUSD=1, \"$command\"")
    fun getGsmFunctionality() = sendCommand("+CFUN?")
    fun getPhoneNumber() = sendCommand("+CNUM")

    // calls
    fun answerCall() = sendCommand("A")

    fun makeCall(number: String) = sendCommand("D$number;")
    fun hangCall() = sendCommand("H")
    fun redial() = sendCommand("DL")

    fun checkPinRequired() = sendCommand("+CPIN?")
    fun sendPin(pin: String) = sendCommand("+CPIN=$pin")
    fun enableCID() = sendCommand("+CLIP=1")
    fun getSignalQuality() = sendCommand("+CSQ")
    fun checkNetworkRegistration() = sendCommand("+CREG?")
    fun getNetworkInfo() = sendCommand("+COPS?")
    fun registerToNetwork() = sendCommand("+CREG=1")
    fun checkGPRSAttachment() = sendCommand("+CGATT?")
    fun bringUpWireless() = sendCommand("+CIICR")
    fun checkLocalIp() = sendCommand("+CIFSR")
    fun enableGPRSData() = sendCommand("+CGATT=1")
    fun setApn(apn: String) = sendCommand("+CSTT=\"$apn\"")
    // sms
    fun readSms(index: Int) = smsTextMode(true).also { sendCommand("+CMGR=$index") }

    fun readAllSms() = smsTextMode(true).also { sendCommand("+CMGL=\"ALL\"") }
    fun deleteSms(index: Int) = sendCommand("+CMGD=$index")
    fun smsTextMode(value: Boolean) = sendCommand("+CMGF=${value.toInt()}")

    fun getProductInformation() = sendCommand("I")
    fun getCurrentConfiguration() = sendCommand("&V")
}
