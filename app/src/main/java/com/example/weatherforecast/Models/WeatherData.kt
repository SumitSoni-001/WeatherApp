package com.example.weatherforecast.Models

data class WeatherData(
    val base: String,
    val cod: Int,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val weather: List<Weather>,
    val wind: Wind
)