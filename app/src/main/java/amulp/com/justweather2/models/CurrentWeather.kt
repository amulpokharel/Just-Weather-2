package amulp.com.justweather2.models

import amulp.com.justweather2.models.subclasses.*

data class CurrentWeather(
        val coord: Coord = Coord(),
        val weather: List<Weather> = emptyList(),
        val base: String = "",
        val main: Main = Main(),
        val wind: Wind = Wind(),
        val clouds: Clouds = Clouds(),
        val dt: Long = 0,
        val sys: Sys = Sys(),
        val id: Long = 0,
        val name: String = "",
        val cod: Long = 0
)