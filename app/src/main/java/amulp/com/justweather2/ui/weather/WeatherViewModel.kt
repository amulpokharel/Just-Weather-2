package amulp.com.justweather2.ui.weather

import amulp.com.justweather2.MyApp
import android.location.Location
import androidx.lifecycle.ViewModel
import amulp.com.justweather2.utils.PrefHelper.get
import amulp.com.justweather2.utils.PrefHelper.set
import amulp.com.justweather2.models.Temperature
import amulp.com.justweather2.models.WeatherResponse
import amulp.com.justweather2.rest.RetrofitClient
import amulp.com.justweather2.rest.WeatherService
import amulp.com.justweather2.utils.PrefHelper.defaultPrefs
import android.content.SharedPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


class WeatherViewModel : ViewModel() {
    private var service: WeatherService
    private var weatherResponse: WeatherResponse? = null
    private val UPDATE_INTERVAL = 600000
    private var disposable = CompositeDisposable()

    //UI Variables
    var weatherIcon = "\uF07B"
    var weatherText = "0 °C"
    var locationName = "Acquiring Location.."
    var humidity = "Humidity"
    var pressure = "Pressure"
    var lastUpdate = "Updated: "

    private var lastChecked: Long?
    private var currentUnit:String?

    private var loc: Location? = null
    private var currentTemp: Temperature? = null


    var dataChanged = false

    private val prefs:SharedPreferences = defaultPrefs(MyApp.getAppContext())

    init {
        lastChecked = prefs["last checked", 0]
        currentUnit = prefs["current unit", "c"]

        weatherText = prefs["weather text", "0 °C"]!!
        humidity = prefs["humidity", "Humidity"]!!
        pressure = prefs["pressure", "Pressure"]!!
        lastUpdate = prefs["last update", "Updated: "]!!
        locationName = prefs["location", "Acquiring Location.."]!!
        weatherIcon = prefs["weather icon", "\uF07B"]!!

        currentTemp = Temperature(prefs["current temp", 0]!!)

        service = RetrofitClient.getClient()
    }

    fun getWeather(location:Location){
        loc = location
        if(canUpdate()) {
            disposable.add(service.getWeather(location.longitude, location.latitude)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result ->
                        weatherResponse = result
                        lastChecked = System.currentTimeMillis()
                        prefs["last checked"] = lastChecked
                        processWeather(result)
                    })
        }
        else
            dataChanged = true
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    private fun processWeather(response: WeatherResponse){
        currentTemp = Temperature(response.main.temp)

        when(currentUnit){
            "c" -> weatherText = currentTemp!!.inCelsius().toString() + " °C"
            "f" -> weatherText = currentTemp!!.inFahrenheit().toString() + " °F"
            "k" -> weatherText = currentTemp!!.inKelvin().toString() + " °K"
        }

        humidity = "Humidity: " + response.main.humidity + " %"
        pressure = "Pressure: " + response.main.pressure + " hpa"
        lastUpdate = "Updated: " + SimpleDateFormat.getDateTimeInstance().format(Date(lastChecked!!))
        locationName = response.name
        weatherIcon = "w" + response.weather!![0].icon
        dataChanged = true

        prefs["current unit"] = currentUnit
        prefs["weather text"] = weatherText
        prefs["humidity"] = humidity
        prefs["pressure"] = pressure
        prefs["last update"] = lastUpdate
        prefs["location"] = locationName
        prefs["weather icon"] = weatherIcon
        prefs["current temp"] = currentTemp!!.inCelsius()
    }

    fun convertTemp() {
        val tempString:String
        when (currentUnit) {
            "c" -> {
                tempString = currentTemp!!.inFahrenheit().toString() + " °F"
                currentUnit = "f"
                prefs["current unit"] = "f"
            }
            "f" -> {
                tempString = currentTemp!!.inKelvin().toString() + " °K"
                currentUnit = "k"
                prefs["current unit"] = "k"
            }
            else -> {
                tempString = currentTemp!!.inCelsius().toString() + " °C"
                currentUnit = "c"
                prefs["current unit"] = "c"
            }
        }

        weatherText = tempString

        prefs["weather text"] = weatherText
    }

    fun canUpdate() : Boolean = System.currentTimeMillis() >= (lastChecked!! + UPDATE_INTERVAL)
}