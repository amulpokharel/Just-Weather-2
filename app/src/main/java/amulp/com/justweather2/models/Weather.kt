package amulp.com.justweather2.models

import com.squareup.moshi.Json

class Weather {

    @Json(name = "id")
    var id: Int = 0
    @Json(name = "main")
    var main: String = ""
    @Json(name = "description")
    var description: String = ""
    @Json(name = "icon")
    var icon: String = ""

}
