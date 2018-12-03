package amulp.com.justweather2.ui.weather

import amulp.com.justweather2.MyApp
import amulp.com.justweather2.R
import amulp.com.justweather2.models.CurrentWeather
import amulp.com.justweather2.models.FutureWeatherElement
import amulp.com.justweather2.models.WeatherList
import amulp.com.justweather2.models.subclasses.Temperature
import amulp.com.justweather2.rest.RetrofitClient
import amulp.com.justweather2.rest.WeatherService
import amulp.com.justweather2.utils.PrefHelper.defaultPrefs
import amulp.com.justweather2.utils.PrefHelper.get
import amulp.com.justweather2.utils.PrefHelper.set
import android.content.SharedPreferences
import android.location.Location
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


class WeatherViewModel : ViewModel(){
    private val service: WeatherService
    private var currentWeather: CurrentWeather? = null
    private var weatherList: WeatherList? = null
    private val UPDATE_INTERVAL = 600000
    private val FUTURE_UPDATE_INTERVAL = 600000

    private val disposable = CompositeDisposable()

    //UI Variables
    var weatherIcon:ObservableField<String> = ObservableField("I")
    var weatherText:ObservableField<String> = ObservableField("text")
    var locationName:ObservableField<String> = ObservableField("Location")
    var humidity:ObservableField<String> = ObservableField("0")
    var pressure:ObservableField<String> = ObservableField("0")
    var lastUpdate:ObservableField<String> = ObservableField("wat")

    var forecastList:List<FutureWeatherElement> = listOf(FutureWeatherElement(),FutureWeatherElement(),FutureWeatherElement(),FutureWeatherElement(),FutureWeatherElement())

    private var lastChecked: Long?
    private var lastFutureChecked: Long?
    private var currentUnit:String?

    private var loc: Location? = null
    private var currentTemp: Temperature? = null
    private val prefs:SharedPreferences = defaultPrefs(MyApp.getAppContext())

    init {
        lastChecked = prefs["last checked", 0]
        lastFutureChecked = prefs["last future checked", 0]
        currentUnit = prefs["current unit", "c"]

        weatherText.set(prefs["weather text", "0 °C"]!!)
        humidity.set(prefs["humidity", "Humidity"]!!)
        pressure.set(prefs["pressure", "Pressure"]!!)
        lastUpdate.set(prefs["last update", "Updated: "]!!)
        locationName.set(prefs["location", "Acquiring Location.."]!!)
        weatherIcon.set(prefs["weather icon", "\uF07B"]!!)

        currentTemp = Temperature(prefs["current temp", 0]!!)

        forecastList[0].apply {
            setTemp(prefs["temp1", 0]!!)
            icon.set(prefs["icon1", "\uF07B"]!!)
            setUnit(currentUnit!!)
        }

        forecastList[1].apply {
            setTemp(prefs["temp2", 0]!!)
            icon.set(prefs["icon2", "\uF07B"]!!)
            setUnit(currentUnit!!)
        }

        forecastList[2].apply {
            setTemp(prefs["temp3", 0]!!)
            icon.set(prefs["icon3", "\uF07B"]!!)
            setUnit(currentUnit!!)
        }

        forecastList[3].apply {
            setTemp(prefs["temp4", 0]!!)
            icon.set(prefs["icon4", "\uF07B"]!!)
            setUnit(currentUnit!!)
        }

        forecastList[4].apply {
            setTemp(prefs["temp5", 0]!!)
            icon.set(prefs["icon5", "\uF07B"]!!)
            setUnit(currentUnit!!)
        }

        service = RetrofitClient.getClient()
    }

    fun getWeather(location:Location){
        loc = location
        if(canUpdate()) {
            disposable.add(service.getWeather(location.longitude, location.latitude)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result ->
                        currentWeather = result
                        lastChecked = System.currentTimeMillis()
                        prefs["last checked"] = lastChecked
                        processWeather(result)
                    })
        }
    }

    fun getFutureWeather(location: Location){
        loc = location
        if(canFutureUpdate()) {
            disposable.add(service.getFutureWeather(location.longitude, location.latitude)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result ->
                        weatherList = result
                        lastFutureChecked = System.currentTimeMillis()
                        prefs["last future checked"] = lastFutureChecked
                        processFutureWeather(result)
                    })
        }
    }

    fun updateUnit(){
        if(prefs["current unit","c"] != currentUnit) {
            currentUnit  = prefs["current unit","c"]
            when(currentUnit){
                "c" -> {
                    weatherText.set(currentTemp!!.inCelsius().toString() + " °C")
                    for(e in forecastList)
                        e.changeToC()
                }
                "f" -> {
                    weatherText.set(currentTemp!!.inFahrenheit().toString() + " °F")
                    for(e in forecastList)
                        e.changeToF()
                }
                "k" -> {
                    weatherText.set(currentTemp!!.inKelvin().toString() + " °K")
                    for(e in forecastList)
                        e.changeToF()
                }
            }
            prefs["weather text"] = weatherText.get()


        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    private fun resolveResource(str:String) : String{
        val resourceID = MyApp.getAppContext().resources.getIdentifier(str, "string", MyApp.getAppContext().packageName)

        return if (resourceID != 0)
            MyApp.getAppContext().getString(resourceID)
        else
            MyApp.getAppContext().getString(R.string.w01d)
    }

    private fun processWeather(currentWeather: CurrentWeather){
        currentTemp = Temperature(currentWeather.main.temp)

        when(currentUnit){
            "c" -> weatherText.set(currentTemp!!.inCelsius().toString() + " °C")
            "f" -> weatherText.set(currentTemp!!.inFahrenheit().toString() + " °F")
            "k" -> weatherText.set(currentTemp!!.inKelvin().toString() + " °K")
        }

        humidity.set("Humidity: " + currentWeather.main.humidity + " %")
        pressure.set("Pressure: " + currentWeather.main.pressure + " hpa")
        lastUpdate.set("Updated: " + SimpleDateFormat.getDateTimeInstance().format(Date(lastChecked!!)))
        locationName.set(currentWeather.name)
        weatherIcon.set(resolveResource("w" + currentWeather.weather[0].icon))
        prefs["current unit"] = currentUnit
        prefs["weather text"] = weatherText.get()
        prefs["humidity"] = humidity.get()
        prefs["pressure"] = pressure.get()
        prefs["last update"] = lastUpdate.get()
        prefs["location"] = locationName.get()
        prefs["weather icon"] = weatherIcon.get()
        prefs["current temp"] = currentTemp!!.inCelsius()
    }

    private fun processFutureWeather(weatherList: WeatherList){
        for (i in 0..4) {
            forecastList[i].apply {
                setValues(weatherList.list[i].main.temp.toInt() , weatherList.list[i].weather[0].icon)
                when(currentUnit){
                    "f" -> changeToF()
                    "k" -> changeToK()
                    else -> changeToC()
                }
            }
        }

        prefs["temp1"] = forecastList[0].getTemp()
        prefs["icon1"] = forecastList[0].icon.get()
        prefs["temp2"] = forecastList[1].getTemp()
        prefs["icon2"] = forecastList[1].icon.get()
        prefs["temp3"] = forecastList[2].getTemp()
        prefs["icon3"] = forecastList[2].icon.get()
        prefs["temp4"] = forecastList[3].getTemp()
        prefs["icon4"] = forecastList[3].icon.get()
        prefs["temp5"] = forecastList[4].getTemp()
        prefs["icon5"] = forecastList[4].icon.get()
    }

    fun canUpdate() : Boolean = System.currentTimeMillis() >= (lastChecked!! + UPDATE_INTERVAL)
    fun canFutureUpdate() : Boolean = System.currentTimeMillis() >= (lastFutureChecked!! + FUTURE_UPDATE_INTERVAL)

}