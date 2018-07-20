package amulp.com.justweather2.models

import amulp.com.justweather2.models.*
import com.squareup.moshi.Json

class WeatherResponse {

    @Json(name = "coord")
    var coord: Coord = Coord()
    @Json(name = "weather")
    var weather: List<Weather>? = null
    @Json(name = "base")
    var base: String = ""
    @Json(name = "main")
    var main: Main = Main()
    @Json(name = "wind")
    var wind: Wind = Wind()
    @Json(name = "clouds")
    var clouds: Clouds = Clouds()
    @Json(name = "dt")
    var dt: Int = 0
    @Json(name = "sys")
    var sys: Sys = Sys()
    @Json(name = "id")
    var id: Int = 0
    @Json(name = "name")
    var name: String = ""
    @Json(name = "cod")
    var cod: Int = 0

}
