package amulp.com.justweather2

import amulp.com.justweather2.ui.settings.SettingsFragment
import amulp.com.justweather2.ui.weather.WeatherFragment
import amulp.com.justweather2.ui.weather.WeatherViewModel
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.afollestad.aesthetic.Aesthetic
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.*


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        title = ""

        Aesthetic.attach(this)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, WeatherFragment.newInstance(), "current")
                    .commit()
        }

        if (Aesthetic.isFirstTime) {
            Aesthetic.config {
                colorPrimary(res = R.color.primaryDarkColor)
                colorPrimaryDark(res = R.color.primaryDarkColor)
                colorWindowBackground(res = R.color.primaryDarkColor)
                textColorPrimary(res = R.color.secondaryTextColor)
                colorNavigationBar(res = R.color.primaryDarkColor)
            }
        }

        setSupportActionBar(toolbar)

        val actionBar = supportActionBar

        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            if(supportFragmentManager.findFragmentByTag("current") is SettingsFragment){
                setHomeAsUpIndicator(R.drawable.ic_back)
            }
            else {
                setHomeAsUpIndicator(R.drawable.ic_location)
            }
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if(supportFragmentManager.findFragmentByTag("current") is SettingsFragment){
                invalidateOptionsMenu()
                actionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
            }
            else { // todo change if more fragments are added
                invalidateOptionsMenu()
                actionBar?.setHomeAsUpIndicator(R.drawable.ic_location)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        attachSharedPrefListener()

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if(!gpsEnabled) {
            alert("Please enable location services for the app to function properly.", "Location seems to be disabled.") {
                okButton { enableLocationSettings() }
                noButton { toast("App will not work without location")}
            }.show()
        }
    }

    override fun onResume() {
        super.onResume()
        Aesthetic.resume(this)
        attachSharedPrefListener()
    }

    override fun onPause() {
        detachSharedPrefListener()
        Aesthetic.pause(this)
        super.onPause()
    }

    override fun onDestroy() {
        detachSharedPrefListener()
        super.onDestroy()
    }

    private fun attachSharedPrefListener() = defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this)
    private fun detachSharedPrefListener() = defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this)


    override fun onSharedPreferenceChanged(prefs:SharedPreferences, key: String) {
        when (key) {
            getString(R.string.pref_dark_mode) -> {
                if (!prefs.getBoolean("Dark Mode", false)) {
                    Aesthetic.config {
                        colorPrimary(res = R.color.primaryDarkColor)
                        colorPrimaryDark(res = R.color.primaryDarkColor)
                        colorWindowBackground(res = R.color.primaryDarkColor)
                        textColorPrimary(res = R.color.secondaryTextColor)
                        colorNavigationBar(res = R.color.primaryDarkColor)
                    }
                }else{
                    Aesthetic.config {
                        colorPrimary(res = R.color.secondaryDarkColor)
                        colorPrimaryDark(res = R.color.secondaryDarkColor)
                        colorWindowBackground(res = R.color.secondaryDarkColor)
                        textColorPrimary(res = R.color.secondaryTextColor)
                        colorNavigationBar(res = R.color.secondaryDarkColor)
                    }
                }
            }
            "current unit" -> {
                viewModel.updateUnit()
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings_update ->{
                supportFragmentManager.beginTransaction()
                        .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
                        .replace(R.id.container, SettingsFragment.newInstance(), "current")
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
