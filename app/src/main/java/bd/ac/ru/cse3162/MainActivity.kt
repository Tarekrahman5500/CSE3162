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
    val api: String = "b3e4b6983f5f6efe30cf9270622a8529"
    private var resultReceiver: ResultReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.locationFind.setOnClickListener {
                //  binding!!.progressBar.visibility = View.VISIBLE

                weatherTask().execute()


            }
        }

        inner class weatherTask() : AsyncTask<String, Void, String>() {

            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: String?): String? {
                val  cITY = binding.spinner.selectedItem.toString()
                // println(binding!!.spinner.selectedItem.toString())
                val response: String? = try {
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$cITY&units=metric&appid=$api").readText(
                        Charsets.UTF_8
                    )

                } catch (e: Exception) {
                    null
                }
                println("ok")
                return response
            }



            @SuppressLint("SetTextI18n")
            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                try {
                    /* Extracting JSON returns from the API */
                    val jsonObj = JSONObject(result)
                    val coord = jsonObj.getJSONObject("coord")
                    val main = jsonObj.getJSONObject("main")
                    val sys = jsonObj.getJSONObject("sys")
                    val wind = jsonObj.getJSONObject("wind")
                    println(wind)
                    val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                    val updatedAt: Long = jsonObj.getLong("dt")
                    val updatedAtText = "Updated at: " + SimpleDateFormat(
                        "dd/MMMM/yyyy hh:mm a",
                        Locale.ENGLISH
                    ).format(Date(updatedAt * 1000))
                    val temp = main.getString("temp")
                    val pressure = main.getString("pressure")
                    val humidity = main.getString("humidity")

                    val sunrise: Long = sys.getLong("sunrise")
                    val sunset: Long = sys.getLong("sunset")
                    val windSpeed = wind.getString("speed")
                    val weatherDescription = weather.getString("description")

                    val address = jsonObj.getString("name") + ", " + sys.getString("country")
                    val lon = coord.getString("lon")
                    val lat = coord.getString("lat")
                    println("$lon $lat")
                    /* Populating extracted data into our views */
                    binding.apply {
                        binding.CityView.text = address
                        binding.datetime.text = updatedAtText
                        binding.description.text = weatherDescription.capitalize()
                        binding.Celcius.text = temp
                        binding.tvSunriseTime.text =
                            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
                        binding.tvSunsetTime.text =
                            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
                        binding.myVisibility.text = "$windSpeed km"
                        binding.pressure.text = "$pressure mBar"
                        binding.humidity.text = "$humidity %"
                        binding.latVAL.text = lat
                        binding.lonVAL.text = lon

                        binding.FarenhiteTemp.text =  (((temp.toDouble()*9)/5)+32).toString()

                    }

                } catch (e: Exception) {
                    //Toast.makeText(con, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }


        }

    }