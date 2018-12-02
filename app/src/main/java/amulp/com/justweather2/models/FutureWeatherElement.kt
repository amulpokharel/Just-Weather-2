package amulp.com.justweather2.models

import amulp.com.justweather2.MyApp
import amulp.com.justweather2.R
import androidx.databinding.ObservableField

data class FutureWeatherElement(var temp: ObservableField<Double> = ObservableField(0.0), var icon:ObservableField<String> = ObservableField("")){
    fun setValues(temp: Int, icon:String){
        this.temp.set(temp)
        this.icon.set(resolveResource(icon))
    }

    private fun resolveResource(str:String) : String{
        val resourceID = MyApp.getAppContext().resources.getIdentifier(str, "string", MyApp.getAppContext().packageName)

        return if (resourceID != 0)
            MyApp.getAppContext().getString(resourceID)
        else
            MyApp.getAppContext().getString(R.string.w01d)
    }
}
