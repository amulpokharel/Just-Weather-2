package amulp.com.justweather2.models

import amulp.com.justweather2.models.subclasses.*

data class WeatherList(
        val cod: String = "",
        val message: String = "",
        val cnt: Long = 0,
        val list: List<WeatherElement> = emptyList(),
        val city: City = City()
)

data class WeatherElement(
        val dt: Long = 0,
        val main: Main = Main(),
        val weather: List<Weather> = emptyList(),
        val clouds: Clouds = Clouds(),
        val wind: Wind = Wind(),
        val rain: Rain = Rain(),
        val sys: SysAlt = SysAlt(),
        val dtTxt: String = ""
)