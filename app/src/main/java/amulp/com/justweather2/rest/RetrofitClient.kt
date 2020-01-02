package amulp.com.justweather2.rest

import amulp.com.justweather2.utils.toastError
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by Amul on 1/9/18.
 */

object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/"
    private var initialized = false
    private lateinit var client:Retrofit

    init {
        generateClient()
    }

    private fun generateClient() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClientBuilder = OkHttpClient.Builder().addInterceptor(interceptor)

        okHttpClientBuilder.addInterceptor(Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)

            doAsync {
                uiThread {
                    when (response.code) {
                        400 -> "Operation Failed".toastError()
                        403 -> "User Error".toastError()
                        404 -> "User or Password Error".toastError()
                    }
                }
            }

            return@Interceptor response
            }
        )

            /*  okHttpClientBuilder.addInterceptor { chain ->
                val oldRequest = chain.request()
                val builder = oldRequest.newBuilder().header("appid", "50aaa0b9c38198d17df8b2140f09879e")
                val newRequest = builder.build()

                chain.proceed(newRequest) }*/
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        client = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClientBuilder.build())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

        initialized = true
    }

    fun getClient(): WeatherService {
        if (!initialized)
            generateClient()
        return client.create(WeatherService::class.java)
    }
}
