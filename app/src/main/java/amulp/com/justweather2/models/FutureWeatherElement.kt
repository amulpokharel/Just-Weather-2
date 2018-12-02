package amulp.com.justweather2.models

import amulp.com.justweather2.MyApp
import amulp.com.justweather2.R
import amulp.com.justweather2.models.subclasses.Temperature
import androidx.databinding.ObservableField

data class FutureWeatherElement(private var _temperature:Temperature = Temperature(0), var tempText: ObservableField<String> = ObservableField(""), var icon:ObservableField<String> = ObservableField("")){
    fun setValues(temp: Int, icon:String){
        _temperature.setTemp(temp)
        this.tempText.set(_temperature.inCelsius().toString() + " °C")
        this.icon.set(resolveResource("w$icon"))
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
