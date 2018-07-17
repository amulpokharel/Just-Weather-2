package com.amulp.justweather

import com.amulp.justweather.ui.settings.SettingsFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amulp.justweather.ui.weather.WeatherFragment
import android.content.Context
import android.location.LocationManager
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.*


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        if(defaultSharedPreferences.getBoolean(getString(R.string.pref_dark_mode), false)) {
            MyApp.darkMode = true
            setTheme(R.style.AppThemeDark)
        }
        else {
            MyApp.darkMode = false
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, WeatherFragment.newInstance(), "current")
                    .commit()
        }

        setSupportActionBar(toolbar)

        val actionBar = supportActionBar

        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            if(supportFragmentManager.findFragmentByTag("current") is SettingsFragment)
                setHomeAsUpIndicator(R.drawable.ic_back)
            else
                setHomeAsUpIndicator(R.drawable.ic_location)
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if(supportFragmentManager.findFragmentByTag("current") is SettingsFragment){
                actionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
                actionBar?.setTitle("Settings")
            }
            else { // todo change if more fragments are added
                actionBar?.setHomeAsUpIndicator(R.drawable.ic_location)
                actionBar?.title = MyApp.currentLocation
            }
        }
    }

    override fun onStart() {
        super.onStart()

        attachSharedPrefListener()

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if(!gpsEnabled) {
            alert("Please enable location services for the app to function properly.", "Location seems to be disabled.") {
                okButton { enableLocationSettings() }
                noButton { toast("App will not work without location")}
            }.show()
        }
    }

    override fun onResume() {
        super.onResume()
        attachSharedPrefListener()
    }

    override fun onPause() {
        super.onPause()
        detachSharedPrefListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        detachSharedPrefListener()
    }

    private fun attachSharedPrefListener() = defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this)
    private fun detachSharedPrefListener() = defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this)


    override fun onSharedPreferenceChanged(prefs:SharedPreferences, key: String) {
        when (key) {
            getString(R.string.pref_dark_mode) -> {
                recreate()
            }
            "current unit" -> {

            }
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
