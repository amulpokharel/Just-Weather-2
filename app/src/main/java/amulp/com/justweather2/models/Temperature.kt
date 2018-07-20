package amulp.com.justweather2.models

/**
 * Created by Amul on 1/24/18.
 */

class Temperature {
    private var tempInC = 0.0

    constructor(temperature: Int) {
        tempInC = temperature.toDouble()
    }

    constructor(temperature: Double) {
        tempInC = temperature
    }

    constructor(temperature: String) {
        tempInC = java.lang.Double.parseDouble(temperature)
    }

    constructor() {
        tempInC = 0.0
    }

    fun setTemp(temperature: Int) {
        tempInC = temperature.toDouble()
    }

    fun setTemp(temperature: Double) {
        tempInC = temperature
    }

    fun setTemp(temperature: String) {
        tempInC = java.lang.Double.parseDouble(temperature)
    }

    //fahrenheit
    fun inFahrenheit(): Int {
        return (tempInC * (9.0 / 5.0) + 32.0).toInt()
    }

    //kelvin
    fun inKelvin(): Int {
        return (tempInC + 273.15).toInt()
    }

    fun inCelsius(): Int {
        return tempInC.toInt()
    }
}