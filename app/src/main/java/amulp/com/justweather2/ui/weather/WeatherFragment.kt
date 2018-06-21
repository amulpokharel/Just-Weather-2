package amulp.com.justweather2.ui.weather

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amulp.com.justweather2.R
import amulp.com.justweather2.databinding.WeatherFragmentBinding
import amulp.com.justweather2.utils.toast
import amulp.com.justweather2.utils.toastError
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.weather_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Thread.sleep
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import amulp.com.justweather2.R.id.weather



class WeatherFragment : Fragment() {

    companion object {
        fun newInstance() = WeatherFragment()
    }

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: WeatherFragmentBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private var longitude = 0.0
    private var latitude = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater,
                R.layout.weather_fragment,
                container,
                false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)

        weather_text.setOnClickListener { viewModel.convertTemp() }
    }

    override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
    }

    private fun checkPermissions() =
            ActivityCompat.checkSelfPermission(activity!!.baseContext, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)) {
            startLocationPermissionRequest()
        } else {
            startLocationPermissionRequest()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
            if(task.isSuccessful && task.result!=null){
                viewModel.getWeather(task.result)
                updateUI()
            }
            else{
                "Couldn't get location".toastError()
            }

        }
    }

    private fun updateUI()
    {
        doAsync {
            while(!viewModel.dataChanged){
                sleep(300)
            }
            uiThread {
                viewModel.dataChanged = false
                weather_icon.text = viewModel.weatherIcon
                weather_text.text = viewModel.weatherText
                location_name.text = viewModel.locationName
                humidity.text = viewModel.humidity
                pressure.text = viewModel.pressure
                last_update.text = viewModel.lastUpdate

                val resourceID = activity!!.applicationContext.resources.getIdentifier(viewModel.weatherIcon, "string", activity!!.packageName)

                if (resourceID != 0)
                    weather_icon.text = getString(resourceID)

            }

        }
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

}
