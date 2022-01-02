package bd.ac.ru.cse3162

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import bd.ac.ru.cse3162.Constants.API_KEY

import bd.ac.ru.cse3162.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var late: Double = 0.0
    var long: Double = 0.0
    var api: String = API_KEY
    private var resultReceiver: ResultReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        resultReceiver = AddressResultReceiver(Handler())
        binding.locationFind.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_LOCATION_PERMISSION
                )
            } else {
                currentLocation
            }
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                currentLocation
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // TODO: Consider calling
    //    ActivityCompat#requestPermissions
    // here to request the missing permissions, and then overriding
    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
    //                                          int[] grantResults)
    // to handle the case where the user grants the permission. See the documentation
    // for ActivityCompat#requestPermissions for more details.

    private val currentLocation: Unit
        get() {
            val locationRequest = LocationRequest()
            locationRequest.interval = 10000
            locationRequest.fastestInterval = 3000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        LocationServices.getFusedLocationProviderClient(this@MainActivity)
                            .removeLocationUpdates(this)
                        if (locationResult.locations.size > 0) {
                            val lastLocationIndex = locationResult.locations.size - 1
                            val latitude = locationResult.locations[lastLocationIndex].latitude
                            val longitude = locationResult.locations[lastLocationIndex].longitude
                            val location = Location("providerNA")
                            location.latitude = latitude
                            location.longitude = longitude
                            late = latitude
                            long = longitude
                            binding.latVAL.text = late.toString()
                            binding.lonVAL.text = long.toString()
                            println("$late $long")
                            //  weatherTask().execute()
                              fetchAddressFromLatLong(location)
                        }
                    }
                }, Looper.getMainLooper())
        }

    private fun fetchAddressFromLatLong(location: Location) {
        val intent = Intent(this, FetchAddressInternetService::class.java)
        intent.putExtra(Constants.RECEIVER, resultReceiver)
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location)
        startService(intent)
    }

    private inner class AddressResultReceiver(handler: Handler?) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            super.onReceiveResult(resultCode, resultData)
            if (resultCode == Constants.SUCCESS_RESULT) {
                 binding.CityView.text = resultData.getString(Constants.RESULT_DATA_KEY)
                println(resultData.getString(Constants.RESULT_DATA_KEY))
                Toast.makeText(this@MainActivity, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT)
            } else {
                Toast.makeText(this@MainActivity, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    companion object {
        private const val REQUEST_CODE_LOCATION_PERMISSION = 1
    }
}
