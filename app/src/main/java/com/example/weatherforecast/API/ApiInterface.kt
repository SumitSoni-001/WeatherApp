package com.example.weatherforecast.API

import com.example.weatherforecast.Models.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("weather")
    fun getData(@Query("q") city: String?, @Query("appid") key: String): Call<WeatherData>

}