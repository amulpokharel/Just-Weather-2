package amulp.com.justweather2.ui.weather

import amulp.com.justweather2.MyApp
import amulp.com.justweather2.R
import amulp.com.justweather2.databinding.WeatherFragmentBinding
import amulp.com.justweather2.utils.toast
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.PermissionChecker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.weather_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.NullPointerException
import java.lang.Thread.sleep


class WeatherFragment : Fragment() {

    companion object {
        fun newInstance() = WeatherFragment()
    }

    private lateinit var viewModel: WeatherViewModel
    private lateinit var locationManager: LocationManager
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        viewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        val binding:WeatherFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.weather_fragment, container, false)
        binding.setLifecycleOwner(this)
        binding.weatherData = viewModel

        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateUnit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home ->{
                    if(viewModel.canUpdate())
                        startUpdate()
                    else
                        "Can't update yet".toast()
                }
            }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        startUpdate()
    }

    private fun startUpdate(){
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
    }

    private fun checkPermissions() =
            checkSelfPermission(activity!!.baseContext, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)
    }

    private fun requestPermissions() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            startLocationPermissionRequest()
        } else {
            startLocationPermissionRequest()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        try {
            if(viewModel.canUpdate()) {
                val loc: Location? = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        ?: throw NullPointerException()
                viewModel.getWeather(loc!!)
            }
        }
        catch (e:Exception)
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        }
    }

    private fun resolveResource(str:String) : String{
        val resourceID = activity!!.applicationContext.resources.getIdentifier(str, "string", activity!!.packageName)

        if (resourceID != 0)
            return getString(resourceID)
        else
            return getString(R.string.w01d)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> Log.i("d", "User interaction was cancelled.")

                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> getLastLocation()

                else -> {
                    "Location Denied".toast()
                    val location = Location("")
                    location.latitude = 0.0
                    location.longitude = 0.0
                    viewModel.getWeather(location)
                }
            }
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        @SuppressLint("MissingPermission")
        override fun onLocationChanged(location: Location) {
            viewModel.getWeather(location)
            locationManager.removeUpdates(this)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

}
