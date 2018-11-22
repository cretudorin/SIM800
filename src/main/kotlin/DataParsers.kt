package sim868.kotlin


class GpsData(gpgStringResponse: String) {

    private val gpsArray = gpgStringResponse.replace("+UGNSINF:", "").split(",")
    private val mapping = listOf(
        "gpsRunStatus",
        "fixStatus",
        "utcDataTime",
        "lat",
        "long",
        "mslAltitude",
        "speedOverGround",
        "courseOverGround",
        "fixMode",
        "reserved1",
        "hdop",
        "pdop",
        "vdop",
        "reserved2",
        "gpsSatellitesInView",
        "gnssSatellitesUsed",
        "glonassStatelitesInView",
        "reserved3",
        "c/n0Max",
        "hpa",
        "vpa"
    )

    private val length = gpsArray.count()

    private val gpsDataMap = gpsArray.associateBy({ mapping[gpsArray.indexOf(it)] }, { it })

    val gpsRunStatus: String by gpsDataMap
    val fixStatus: String by gpsDataMap
    val utcDataTime: String by gpsDataMap
    val lat: String by gpsDataMap
    val long: String  by gpsDataMap
    val mslAltitude: String by gpsDataMap
    val speedOverGround: String by gpsDataMap
    val courseOverGround: String by gpsDataMap// degrees
    val fixMode: String by gpsDataMap
    val reserved1: String by gpsDataMap
    val hdop: String by gpsDataMap  // Horizontal Dilution of Precision
    val pdop: String by gpsDataMap // Position Dilution of Precision
    val vdop: String by gpsDataMap // Vertical Dilution of Precision
    val reserved2: String by gpsDataMap
    val gpsSatellitesInView: String by gpsDataMap
    val gnssSatellitesUsed: String by gpsDataMap
    val glonassStatelitesInView: String by gpsDataMap
    val reserved3: String by gpsDataMap
    val cn0Max: String by gpsDataMap
    val hpa: String by gpsDataMap // Horizontal Position Accuracy
    val vpa: String by gpsDataMap // Vertical Position Accuracy

}
