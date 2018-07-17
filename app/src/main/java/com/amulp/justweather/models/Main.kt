package com.amulp.justweather.models

import com.squareup.moshi.Json

class Main {

    @Json(name = "temp")
    var temp: Double = 0.0
    @Json(name = "pressure")
    var pressure: Double = 0.0
    @Json(name = "humidity")
    var humidity: Int = 0
    @Json(name = "temp_min")
    var tempMin: Double = 0.0
    @Json(name = "temp_max")
    var tempMax: Double = 0.0
    @Json(name = "sea_level")
    var seaLevel: Double = 0.0
    @Json(name = "grnd_level")
    var grndLevel: Double = 0.0

}
