package com.amulp.justweather.ui.weather

import com.amulp.justweather.MyApp
import android.location.Location
import androidx.lifecycle.ViewModel
import com.amulp.justweather.utils.PrefHelper.get
import com.amulp.justweather.utils.PrefHelper.set
import com.amulp.justweather.models.Temperature
import com.amulp.justweather.models.WeatherResponse
import com.amulp.justweather.rest.RetrofitClient
import com.amulp.justweather.rest.WeatherService
import com.amulp.justweather.utils.PrefHelper.defaultPrefs
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

    fun updateUnit(){
        if(prefs["current unit","c"] != currentUnit)
        {
            currentUnit  = prefs["current unit","c"]
            when(currentUnit){
                "c" -> weatherText = currentTemp!!.inCelsius().toString() + " °C"
                "f" -> weatherText = currentTemp!!.inFahrenheit().toString() + " °F"
                "k" -> weatherText = currentTemp!!.inKelvin().toString() + " °K"
            }

            prefs["weather text"] = weatherText
        }
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

    fun canUpdate() : Boolean = System.currentTimeMillis() >= (lastChecked!! + UPDATE_INTERVAL)
}