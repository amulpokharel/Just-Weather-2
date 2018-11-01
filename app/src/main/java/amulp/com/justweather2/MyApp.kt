package amulp.com.justweather2
import android.app.Application
import android.content.Context

class MyApp : Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: MyApp? = null
        var darkMode = false

        @JvmStatic
        fun getAppContext() : Context =  instance!!.applicationContext
    }
}