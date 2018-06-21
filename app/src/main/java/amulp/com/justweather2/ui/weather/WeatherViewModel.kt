package amulp.com.justweather2.ui.weather


import amulp.com.justweather2.MyApp
import android.content.SharedPreferences
import android.location.Location
import androidx.lifecycle.ViewModel
import amulp.com.justweather2.models.Temperature
import amulp.com.justweather2.models.WeatherResponse
import amulp.com.justweather2.rest.RetrofitClient
import amulp.com.justweather2.rest.WeatherService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*




class WeatherViewModel : ViewModel() {
    private lateinit var service: WeatherService
    private var weatherResponse: WeatherResponse? = null
    private val UPDATE_INTERVAL = 600000

    //UI Variables
    var weatherIcon = "\uF07B"
    var weatherText = "0 °C"
    var locationName = "Acquiring Location.."
    var humidity = "Humidity"
    var pressure = "Pressure"
    var lastUpdate = "Updated: "

    var lastChecked: Long = 0
    var loc: Location? = null
    var currentTemp: Temperature? = null
    var currentUnit = "c"

    var dataChanged = false

    private var sharedPref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    init {
        lastChecked =  0 //sharedPref!!.getLong("last update", 0)
        currentUnit = "c" //sharedPref!!.getString("current temp", "c")
        service = RetrofitClient.getClient()
    }

    fun getWeather(location:Location){
        loc = location
        if(System.currentTimeMillis() >= (lastChecked + UPDATE_INTERVAL)) {
            service.getWeather(location.longitude, location.latitude)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result ->
                        lastChecked = System.currentTimeMillis()
                        processWeather(result)
                    }
        }
        else
            dataChanged = true
    }

    private fun processWeather(response: WeatherResponse){
        currentTemp = Temperature(response.main.temp)

        when(currentUnit){
            "c" -> weatherText = currentTemp!!.inCelsius().toString() + " °C"
            "f" -> weatherText = currentTemp!!.inCelsius().toString() + " °F"
            "k" -> weatherText = currentTemp!!.inCelsius().toString() + " °K"
        }

        humidity = "Humidity: " + response.main.humidity + " %"
        pressure = "Pressure: " + response.main.pressure + " hpa"
        lastUpdate = "Updated: " + SimpleDateFormat.getDateTimeInstance().format(Date(lastChecked))
        locationName = response.name

        weatherIcon = "w" + response.weather!![0].icon

        dataChanged = true
        //set icons
    }

    fun convertTemp() {
        val tempString:String
        if (currentUnit == "c") {
            tempString = currentTemp!!.inFahrenheit().toString() + " °F"
            currentUnit = "f"
        } else if (currentUnit == "f") {
            tempString = currentTemp!!.inKelvin().toString() + " °K"
            currentUnit = "k"
        } else {
            tempString = currentTemp!!.inCelsius().toString() + " °C"
            currentUnit = "c"
        }

        weatherText = tempString
    }


}
