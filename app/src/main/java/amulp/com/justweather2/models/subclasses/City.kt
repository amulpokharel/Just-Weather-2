package amulp.com.justweather2.models.subclasses

data class City(
        val id: Long = 0,
        val name: String = "",
        val coord: Coord = Coord(),
        val country: String = "",
        val population: Long = 0
)