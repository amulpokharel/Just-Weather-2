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
import androidx.appcompat.app.ActionBar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.*


class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    var currentItem = R.id.nav_weather

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, WeatherFragment.newInstance())
                    .commitNow()
        }
        mDrawerLayout = findViewById(R.id.drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        navigationView.setCheckedItem(R.id.nav_weather)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()

            if (currentItem != menuItem.itemId)
            when(menuItem.itemId){
                R.id.nav_weather -> {
                    currentItem = R.id.nav_weather
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.container, WeatherFragment.newInstance())
                            .commitNow()
                }
                R.id.nav_settings ->{
                    currentItem = R.id.nav_settings
                    title = "Settings"
                    toolbar.menu
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.container, SettingsFragment.newInstance())
                            .commitNow()
                }
            }

            true
        }
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_burger)
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
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
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
