package com.example.weatherforecast.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiController {

    companion object {
        var RETROFIT: Retrofit? = null

        fun getApi(): Retrofit? {

            if (RETROFIT == null) {
                RETROFIT = Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return RETROFIT
        }
    }

}