package sim800.kotlin

class Sim800Commands {

    companion object {

        val echoOn = "E1"
        val echoOff = "E0"
        val resendLastCommand = "A/"
        val productInformation = "I"
        val monitorSpeakerLoudness = "L"
        val monitorSpeakerMode = "M"
        val dataModeToCommandMode = "+++"
        val comandModeToDataMode = "O"
        val pulseDialing = "P"
        val numberOfRingsBeforeAutoAnsering = "S0"
        val commandLineTerminationChar = "S3"
        val responseFormatingChar = "S4"
        val commandLineEditingChar = "S5"
        val pauseBeforeBlindDialing = "S6"
        val secondsWaitForConnectionCompletion = "S7"
        val disconnectDelayonNoDataCarrier = "S10"
        val toneDialing = "T"
        val taResponseFormat = "V"
        val resetDefaultConfig = "Z"
        val dcdFunctionMode = "&C"
        val dtrFunctionMode = "&D"
        val factoryDefaultConfig = "&F"
        val currentConfig = "&V"
        val storeActiveProfile = "&W"
        val taCapabilitiesList = "+GCAP"
        val manufacturerIdentification = "+GMI"
        val taModelIdentification = "+GMM"
        val requestIMEI = "+GSN"
        val teTaContorolFrame = "+ICF"
        val teTAFixedLocalRate = "+IPR"
        val diconnectVoiceCall = "+HVOIC"


        // GPS
        val gpsState = "+CGNSPWR"
        val getCurrentPosition = "+CGNSINF"
        val getPositionOnInterval = "+CGNSURC"

        // GSM
        val networkScan = "+COPS"
        val gsmBand = "+CBAND"
        val ussd = "+CUSD"
        val phoneFunctionality = "+CFUN"
        val currentPhoneNumber = "+CNUM"
        val answerCall = "A"
        val redial = "DL"
        val disconnect = "H"
        fun dial(number: String) = "D$number;"

        val pin = "+CPIN"
        val CIP = "+CLIP"
        val signalQuality = "+CSQ"
        val networkRegistration = "+CREG"
        val gprsAttachment = "+CGATT"
        val wirelessStatus = "+CIICR"
        val localIP = "+CIFSR"
        fun setAPN(apn: String, user: String = "", password: String = "") = """+CSTT="$apn","$user","$password""""
        val smsMessageFormat = "+CMGF"
        val readSMS = "+CMGR"
        val allSMS = """+CMGL="ALL""""
        val deleteSMS = "+CMGD"
        val productInfo = "I"
        val setBearer = "+SAPBR"

        // http
        val initHttpService = "+HTTPINIT"
        val stopHttpService = "+HTTPTERM"
        val setHttpParam = "+HTTPPARA"
        val httpRead = "+HTTPREAD"
        val doGetRequest = "+HTTPACTION=0"
        val doPostRequest = "+HTTPACTION=1"
        val doHeadRequest = "+HTTPACTION=2"
        val doDeleteRequest = "+HTTPACTION=3"
        val httpInputData = "+HTTPDATA"
    }
}

class SIM800Responses {
    companion object {
        val gpsInfo = "UGNSINF"
        val newSms = "CMTI"
        val httpResponse = "+HTTPREAD:"
    }
}