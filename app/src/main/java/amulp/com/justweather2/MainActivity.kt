package amulp.com.justweather2

import amulp.com.justweather2.ui.settings.SettingsFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import amulp.com.justweather2.ui.weather.WeatherFragment
import android.content.Context
import android.location.LocationManager
import android.content.Intent
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.*


class MainActivity : AppCompatActivity() {

    var currentLocation = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, WeatherFragment.newInstance(), "weather")
                    .commitNow()
        }
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_location)
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if(supportFragmentManager.findFragmentByTag("settings") is SettingsFragment){
                actionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
                currentLocation  = actionBar?.title.toString()
                actionBar?.setTitle("Settings")
            }
            else { // todo change if more fragments are added
                actionBar?.setHomeAsUpIndicator(R.drawable.ic_location)
                actionBar?.title = currentLocation
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings_update ->{
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, SettingsFragment.newInstance(), "settings")
                        .addToBackStack(null)
                        .commit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun enableLocationSettings() {
        val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(settingsIntent)
    }

}
