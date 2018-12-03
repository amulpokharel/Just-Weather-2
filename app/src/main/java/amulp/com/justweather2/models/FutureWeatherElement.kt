package amulp.com.justweather2.models

import amulp.com.justweather2.MyApp
import amulp.com.justweather2.R
import amulp.com.justweather2.models.subclasses.Temperature
import androidx.databinding.ObservableField

data class FutureWeatherElement(private var _temperature:Temperature = Temperature(0), var tempText: ObservableField<String> = ObservableField(""), var icon:ObservableField<String> = ObservableField("")){
    private var currentUnit:String = "c"
    fun setValues(temp: Int, icon:String){
        _temperature.setTemp(temp)
        this.tempText.set(_temperature.inCelsius().toString() + " °C")
        this.icon.set(resolveResource("w$icon"))
    }

    fun getTemp() = _temperature.inCelsius()
    fun setUnit(str:String){
        currentUnit = str
        when(currentUnit.toLowerCase()){
            "c" -> changeToC()
            "f" -> changeToF()
            "k" -> changeToK()
            else -> throw Exception("Something went wrong")
        }
    }
    fun setTemp(temp:Int) {
        _temperature.setTemp(temp)
        when(currentUnit.toLowerCase()){
            "c" -> changeToC()
            "f" -> changeToF()
            "k" -> changeToK()
            else -> throw Exception("Something went wrong")
        }
    }

    fun changeToF() = this.tempText.set(_temperature.inFahrenheit().toString() + " °F")
    fun changeToC() = this.tempText.set(_temperature.inCelsius().toString() + " °C")
    fun changeToK() = this.tempText.set(_temperature.inKelvin().toString() + " °K")

    private fun resolveResource(str:String) : String{
        val resourceID = MyApp.getAppContext().resources.getIdentifier(str, "string", MyApp.getAppContext().packageName)
        return if (resourceID != 0)
            MyApp.getAppContext().getString(resourceID)
        else
            MyApp.getAppContext().getString(R.string.w01d)
    }
}