package com.example.weatherforecast

import android.app.Dialog
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.weatherforecast.API.ApiController
import com.example.weatherforecast.API.ApiInterface
import com.example.weatherforecast.Models.*
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var CITY: String? = "Hisar"
    private val APIKEY = "164b1615eaacb604b80eda6da0052d19"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setNavigationBarColor(getResources().getColor(R.color.black))
            window.navigationBarColor = ContextCompat.getColor(this@MainActivity, R.color.black)
        }

        getMyData(CITY)

        // Bottom Dialog :- To search a new city
        val dialog = BottomSheetDialog(this)
        val dialogViews = dialog.layoutInflater.inflate(R.layout.city_dialog, null)
        dialog.setCancelable(false)
        dialog.setContentView(dialogViews)

        binding.editLocation.setOnClickListener(View.OnClickListener {
            dialog.show()
        })

        dialogViews.findViewById<TextView>(R.id.ok).setOnClickListener(View.OnClickListener {
            val etCity = dialogViews.findViewById<EditText>(R.id.et_city)
            if (!etCity.text.toString().isEmpty()) {
                CITY = etCity.text.toString()
                Toast.makeText(applicationContext, "City : ${etCity.text}", Toast.LENGTH_SHORT)
                    .show()
                getMyData(CITY)
                etCity.text.clear()
                dialog.dismiss()
            } else {
                etCity.setError("Enter your city")
            }
        })

        dialogViews.findViewById<TextView>(R.id.cancel).setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

    }

    private fun getMyData(city: String?) {
        val retrofit = ApiController.getApi()
        val apiInterface = retrofit?.create(ApiInterface::class.java)

        apiInterface?.getData(city, APIKEY)?.enqueue(object : Callback<WeatherData> {

            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.isSuccessful) {
                    /** Getting values(response) from API */
                    // ? is for safeCall or we can also use !! which shows non-null
                    val main = response.body()?.main
                    val sys = response.body()?.sys
                    val wind = response.body()?.wind
                    val weather = response.body()?.weather?.get(0)

                    val status = weather?.main      // Clouds , Mist , Clear , Haze , Rain ,
                    val updatedAt = response.body()?.dt
                    val updatedAtText = "Updated at : " + SimpleDateFormat(
                        "dd/MM/yy hh:mm a",
                        Locale.ENGLISH
                    ).format(Date((updatedAt?.toLong())!! * 1000))
                    val temp = main?.temp?.minus(273.15)?.toFloat()
                    val feelsLike = main?.feels_like?.minus(273.15)?.toFloat()
                    val pressure = main?.pressure
                    val humidity = main?.humidity

                    val sunrise = sys?.sunrise
                    val sunset = sys?.sunset
                    val windSpeed = wind?.speed?.times(3.6)?.toFloat()

                    val address = response.body()?.name + ", " + sys?.country

                    /** Setting values in views. */
                    findViewById<TextView>(R.id.address).text = address
                    findViewById<TextView>(R.id.updated_at).text = updatedAtText
                    findViewById<TextView>(R.id.weatherStatus).text = status
                    findViewById<TextView>(R.id.temp).text = "$temp °C"
                    findViewById<TextView>(R.id.feels_like).text = "$feelsLike °C"
                    findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat(
                        "hh:mm a",
                        Locale.ENGLISH
                    ).format(Date((sunrise?.toLong())!! * 1000)/*Date(sunset * 1000)*/)
                    findViewById<TextView>(R.id.sunset).text = SimpleDateFormat(
                        "hh:mm a",
                        Locale.ENGLISH
                    ).format(Date((sunset?.toLong())!! * 1000))
                    findViewById<TextView>(R.id.wind).text = "$windSpeed km/h"
                    findViewById<TextView>(R.id.pressure).text = "${pressure.toString()} hpa"
                    findViewById<TextView>(R.id.humidity).text = "${humidity.toString()} %"

                    /** Setting Animations acc. to Weather Status */
                    when (status) {
                        "Clouds" -> {
                            binding.anime.setAnimation(R.raw.cloudy)
                            binding.anime.playAnimation()
                        }
                        "Rain" -> {
                            binding.anime.setAnimation(R.raw.rain)
                            binding.anime.speed.times(2)
                            binding.anime.playAnimation()
                        }
                        "Clear" -> {
                            binding.anime.setAnimation(R.raw.sun)
                            binding.anime.playAnimation()
                        }
                        "Mist" -> {
                            binding.anime.setAnimation(R.raw.mist)
                            binding.anime.playAnimation()
                        }
                        "Haze" -> {
                            binding.anime.setAnimation(R.raw.mist)
                            binding.anime.playAnimation()
                        }
                        "Snow" -> {
                            binding.anime.setAnimation(R.raw.snowfall)
                            binding.anime.playAnimation()
                        }
                        "Storm" -> {
                            binding.anime.setAnimation(R.raw.storm)
                            binding.anime.playAnimation()
                        }
                        "Extreme" -> {
                            binding.anime.setAnimation(R.raw.storm)
                            binding.anime.playAnimation()
                        }
                    }

                }
            }

            override fun onFailure(call: retrofit2.Call<WeatherData>, t: kotlin.Throwable) {
                Toast.makeText(
                    applicationContext,
                    t.localizedMessage.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

}
