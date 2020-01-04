package amulp.com.justweather2

import android.appwidget.AppWidgetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RemoteViews

class WidgetConfigureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_configure)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(this)

        RemoteViews(this.packageName, R.layout.just_weather_widget).also { views->
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

/*        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(Activity.RESULT_OK, resultValue)
        finish()*/
    }
}