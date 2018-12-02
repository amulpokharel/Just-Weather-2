package amulp.com.justweather2.rest

import amulp.com.justweather2.models.CurrentWeather
import amulp.com.justweather2.models.WeatherList
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Amul on 1/22/18.
 */

interface WeatherService {

    @GET("data/2.5/weather?appid=50aaa0b9c38198d17df8b2140f09879e&units=metric")
    fun getWeather(@Query("lon") lon: Double, @Query("lat") lat: Double): Single<CurrentWeather>

    @GET("data/2.5/forecast?appid=50aaa0b9c38198d17df8b2140f09879e&units=metric")
    fun getFutureWeather(@Query("lon") lon: Double, @Query("lat") lat: Double): Single<WeatherList>}
