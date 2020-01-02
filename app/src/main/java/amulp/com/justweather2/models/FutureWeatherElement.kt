package amulp.com.justweather2.models

import amulp.com.justweather2.MyApp
import amulp.com.justweather2.R
import amulp.com.justweather2.models.subclasses.Temperature
import android.annotation.SuppressLint
import androidx.databinding.ObservableField
import java.text.SimpleDateFormat
import java.util.*

data class FutureWeatherElement(private var _temperature:Temperature = Temperature(0), var tempText: ObservableField<String> = ObservableField(""), var icon:ObservableField<String> = ObservableField(""), var time:ObservableField<String> = ObservableField("")){
    private var currentUnit:String = "c"
    @SuppressLint("SimpleDateFormat")
    fun setValues(temp: Int, icon:String, time:Long){
        _temperature.setTemp(temp)
        this.tempText.set(_temperature.inCelsius().toString() + " °C")
        this.time.set(SimpleDateFormat("h:mm a").format(Date(time * 1000)))
        this.icon.set(resolveResource("w$icon"))
    }

    fun getTemp() = _temperature.inCelsius()
    fun setUnit(str:String){
        currentUnit = str
        when (currentUnit.toLowerCase(Locale.ROOT)){
            "c" -> changeToC()
            "f" -> changeToF()
            "k" -> changeToK()
            else -> throw Exception("Something went wrong")
        }
    }
    fun setTemp(temp:Int) {
        _temperature.setTemp(temp)
        when(currentUnit.toLowerCase(Locale.ROOT)) {
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
