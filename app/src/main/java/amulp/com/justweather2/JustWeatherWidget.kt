package amulp.com.justweather2

import amulp.com.justweather2.models.Constants
import amulp.com.justweather2.models.subclasses.Temperature
import amulp.com.justweather2.rest.RetrofitClient
import amulp.com.justweather2.rest.WeatherService
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.RemoteViews
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class JustWeatherWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        @SuppressLint("MissingPermission")
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.just_weather_widget)
            views.setOnClickPendingIntent(R.id.weather_text,
                getPendingIntent(context, 0))

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getPendingIntent(context: Context, value: Int): PendingIntent {
            val intent = Intent(context, MainActivity::class.java)
            intent.action = Constants.UPDATE_WEATHER
            return PendingIntent.getActivity(context, value, intent, 0)
        }

    }
}

