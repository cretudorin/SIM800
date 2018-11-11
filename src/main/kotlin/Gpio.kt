package sim868.kotlin

import com.pi4j.io.gpio.*


class Gpio(pinNumber: Pin, name: String = "None") {

    private var gpio = GpioFactory.getInstance()
    private var pin = gpio.provisionDigitalOutputPin(pinNumber, name).also {
        it.setShutdownOptions(true, PinState.LOW)
    }
    fun setLow() = pin.low()
    fun setHigh() = pin.high()
    fun toggle() = pin.toggle()
    fun pulse(duration: Long, blocking: Boolean) = pin.pulse(duration, blocking)
    fun release() = gpio.shutdown()
}
