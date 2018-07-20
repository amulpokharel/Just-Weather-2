package amulp.com.justweather2.models

import com.squareup.moshi.Json

class Sys {

    @Json(name = "message")
    var message: Double = 0.0
    @Json(name = "country")
    var country: String = ""
    @Json(name = "sunrise")
    var sunrise: Int = 0
    @Json(name = "sunset")
    var sunset: Int = 0

}
