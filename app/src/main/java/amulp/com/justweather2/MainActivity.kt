package amulp.com.justweather2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import amulp.com.justweather2.ui.weather.WeatherFragment
import android.content.Context
import android.location.LocationManager
import android.content.Intent
import android.provider.Settings
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, WeatherFragment.newInstance())
                    .commitNow()
        }

        setSupportActionBar(toolbar)
        toolbar.title = "Getting Location..."
    }

    override fun onStart() {
        super.onStart()

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if(!gpsEnabled) {
            alert("Please enable location services for the app to function properly.", "Location seems to be disabled.") {
                okButton { enableLocationSettings() }
                noButton { toast("App will not work without location")}
            }.show()
        }
    }

    private fun enableLocationSettings() {
        val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(settingsIntent)
    }

}
