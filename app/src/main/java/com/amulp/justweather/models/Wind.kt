package com.amulp.justweather.models

import com.squareup.moshi.Json

class Wind {

    @Json(name = "speed")
    var speed: Double = 0.0
    @Json(name = "deg")
    var deg: Double = 0.0

}
