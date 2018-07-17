package com.amulp.justweather.models

import com.squareup.moshi.Json

class Coord {

    @Json(name = "lon")
    var lon: Double = 0.0
    @Json(name = "lat")
    var lat: Double = 0.0

}
