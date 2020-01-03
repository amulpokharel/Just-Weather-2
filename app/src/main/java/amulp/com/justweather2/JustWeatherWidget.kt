package amulp.com.justweather2

import amulp.com.justweather2.models.CurrentWeather
import amulp.com.justweather2.models.subclasses.Temperature
import amulp.com.justweather2.rest.RetrofitClient
import amulp.com.justweather2.utils.PrefHelper
import amulp.com.justweather2.utils.PrefHelper.get
import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.RemoteViews
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class JustWeatherWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        @SuppressLint("MissingPermission")
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


            val locationListener: LocationListener = object : LocationListener {
                @SuppressLint("MissingPermission")
                override fun onLocationChanged(location: Location) {
                    updateWeather(location, context)
                    locationManager.removeUpdates(this)
                }
                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            if(gpsEnabled){
                try {
                    val loc: Location? = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            ?: throw NullPointerException()
                    updateWeather(loc!!, context)
                }
                catch (e:Exception) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                }

            }

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.just_weather_widget)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun updateWeather(loc:Location, context: Context){
            val service = RetrofitClient.getClient()
            GlobalScope.launch(Dispatchers.IO){
                val result = service.getWeather(loc.longitude, loc.latitude)
                val currentTemp = Temperature(result.main.temp)
                val prefs: SharedPreferences = PrefHelper.defaultPrefs(MyApp.getAppContext())

                val tempText = when(prefs["current unit", "c"]){
                    "c" -> currentTemp.inCelsius().toString() + " °C"
                    "f" -> currentTemp.inFahrenheit().toString() + " °F"
                    "k" -> currentTemp.inKelvin().toString() + " °K"
                    else -> "0 °C"
                }
                processWeather(tempText)
            }
        }

        private fun processWeather(currentWeather: String){
            val prefs: SharedPreferences = PrefHelper.defaultPrefs(MyApp.getAppContext())

            //check sharedpref for unit, update accordingly
/*            when(prefs["current unit", "c"]){
                "c" -> weatherText.set(currentTemp.inCelsius().toString() + " °C")
                "f" -> weatherText.set(currentTemp.inFahrenheit().toString() + " °F")
                "k" -> weatherText.set(currentTemp.inKelvin().toString() + " °K")
            }*/
        }
    }
}

