package amulp.com.justweather2.ui.weather

import amulp.com.justweather2.MyApp
import amulp.com.justweather2.R
import amulp.com.justweather2.models.CurrentWeather
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
                "c" -> weatherText.set(currentTemp!!.inCelsius().toString() + " °C")
                "f" -> weatherText.set(currentTemp!!.inFahrenheit().toString() + " °F")
                "k" -> weatherText.set(currentTemp!!.inKelvin().toString() + " °K")
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
        print(weatherList)
    }

    fun canUpdate() : Boolean = System.currentTimeMillis() >= (lastChecked!! + UPDATE_INTERVAL)
    fun canFutureUpdate() : Boolean = System.currentTimeMillis() >= (lastFutureChecked!! + FUTURE_UPDATE_INTERVAL)

}