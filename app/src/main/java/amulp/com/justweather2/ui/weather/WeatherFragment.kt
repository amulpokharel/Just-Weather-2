package amulp.com.justweather2.ui.weather

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
import android.view.*
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.PermissionChecker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

class WeatherFragment : Fragment() {

    companion object {
        fun newInstance() = WeatherFragment()
    }

    private lateinit var viewModel: WeatherViewModel
    private lateinit var locationManager: LocationManager
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        viewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        val binding:WeatherFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.weather_fragment, container, false)
        binding.setLifecycleOwner(this)
        binding.weatherData = viewModel

        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateUnit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
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
            val loc: Location? = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    ?: throw NullPointerException()

            if(viewModel.canUpdate()) {
                viewModel.getWeather(loc!!)
            }
            if (viewModel.canFutureUpdate()){
                viewModel.getFutureWeather(loc!!)
            }
        }
        catch (e:Exception) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        }
    }

    private fun hasAllPermissionsGranted(grantResults: IntArray): Boolean {
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if(hasAllPermissionsGranted(grantResults)){
            getLastLocation()
        }
        else{
            Log.i("d", "User interaction was cancelled.")
            "Location Denied".toast()
            val location = Location("").apply {
                latitude = 0.0
                longitude = 0.0
            }
            viewModel.getWeather(location)
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
