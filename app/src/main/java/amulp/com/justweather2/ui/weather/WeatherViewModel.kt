package amulp.com.justweather2.ui.weather

import amulp.com.justweather2.MyApp
import amulp.com.justweather2.R
import amulp.com.justweather2.models.Temperature
import amulp.com.justweather2.models.WeatherResponse
import amulp.com.justweather2.rest.RetrofitClient
import amulp.com.justweather2.rest.WeatherService
import amulp.com.justweather2.utils.PrefHelper.defaultPrefs
import amulp.com.justweather2.utils.PrefHelper.get
import amulp.com.justweather2.utils.PrefHelper.set
import android.content.SharedPreferences
import android.location.Location
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


class WeatherViewModel : ViewModel(){
    private val service: WeatherService
    private var weatherResponse: WeatherResponse? = null
    private val UPDATE_INTERVAL = 600000
    private val disposable = CompositeDisposable()

    //UI Variables
    var weatherIcon:ObservableField<String> = ObservableField("I")
    var weatherText:ObservableField<String> = ObservableField("text")
    var locationLiveData:MutableLiveData<String> = MutableLiveData()
    private var locationName = "Location"
        set(value) {
            field = value
            locationLiveData.postValue(value)
        }
    var humidity:ObservableField<String> = ObservableField("0")
    var pressure:ObservableField<String> = ObservableField("0")
    var lastUpdate:ObservableField<String> = ObservableField("wat")


    private var lastChecked: Long?
    private var currentUnit:String?

    private var loc: Location? = null
    private var currentTemp: Temperature? = null
    private val prefs:SharedPreferences = defaultPrefs(MyApp.getAppContext())

    init {
        lastChecked = prefs["last checked", 0]
        currentUnit = prefs["current unit", "c"]

        weatherText.set(prefs["weather text", "0 °C"]!!)
        humidity.set(prefs["humidity", "Humidity"]!!)
        pressure.set(prefs["pressure", "Pressure"]!!)
        lastUpdate.set(prefs["last update", "Updated: "]!!)
        locationName = prefs["location", "Acquiring Location.."]!!
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
                        weatherResponse = result
                        lastChecked = System.currentTimeMillis()
                        prefs["last checked"] = lastChecked
                        processWeather(result)
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

        if (resourceID != 0)
            return MyApp.getAppContext().getString(resourceID)
        else
            return MyApp.getAppContext().getString(R.string.w01d)
    }

    private fun processWeather(response: WeatherResponse){
        currentTemp = Temperature(response.main.temp)

        when(currentUnit){
            "c" -> weatherText.set(currentTemp!!.inCelsius().toString() + " °C")
            "f" -> weatherText.set(currentTemp!!.inFahrenheit().toString() + " °F")
            "k" -> weatherText.set(currentTemp!!.inKelvin().toString() + " °K")
        }

        humidity.set("Humidity: " + response.main.humidity + " %")
        pressure.set("Pressure: " + response.main.pressure + " hpa")
        lastUpdate.set("Updated: " + SimpleDateFormat.getDateTimeInstance().format(Date(lastChecked!!)))
        locationName = response.name
        locationLiveData.postValue(response.name)
        weatherIcon.set(resolveResource("w" + response.weather!![0].icon))
        prefs["current unit"] = currentUnit
        prefs["weather text"] = weatherText.get()
        prefs["humidity"] = humidity.get()
        prefs["pressure"] = pressure.get()
        prefs["last update"] = lastUpdate.get()
        prefs["location"] = locationName
        prefs["weather icon"] = weatherIcon.get()
        prefs["current temp"] = currentTemp!!.inCelsius()
    }

    fun canUpdate() : Boolean = System.currentTimeMillis() >= (lastChecked!! + UPDATE_INTERVAL)
}