package com.amulp.justweather.rest

import retrofit2.http.GET
import retrofit2.http.Query

import com.amulp.justweather.models.WeatherResponse
import io.reactivex.Single

/**
 * Created by Amul on 1/22/18.
 */

interface WeatherService {

    @GET("data/2.5/weather?appid=50aaa0b9c38198d17df8b2140f09879e&units=metric")
    fun getWeather(@Query("lon") lon: Double, @Query("lat") lat: Double): Single<WeatherResponse>
}
