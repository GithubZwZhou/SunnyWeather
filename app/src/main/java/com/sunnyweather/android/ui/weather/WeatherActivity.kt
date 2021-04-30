package com.sunnyweather.android.ui.weather

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import com.sunnyweather.android.ui.place.WeatherViewModel
import com.sunnyweather.android.util.makeToast
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "WeatherActivity"
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_weather)

        viewModel.apply {
            if (locationLng.isEmpty()) {
                locationLng = intent.getStringExtra("location_lng") ?: ""
            }
            if (locationLat.isEmpty()) {
                locationLat = intent.getStringExtra("location_lat") ?: ""
            }
            if (placeName.isEmpty()) {
                placeName = intent.getStringExtra("place_name") ?: ""
            }
        }
        viewModel.weatherLiveData.observe(this) { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                "无法获取天气信息".makeToast(this)
                result.exceptionOrNull()?.printStackTrace()
            }
        }
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }

    private fun showWeatherInfo(weather: Weather) {
        val (realtime, daily) = weather

        findViewById<TextView>(R.id.placeName).text = viewModel.placeName

        "${realtime.temperature.toInt()} ${8451.toChar()}".let {
            findViewById<TextView>(R.id.currentTemp).text = it
        }

        Log.d(TAG, realtime.toString())
        findViewById<TextView>(R.id.currentSky).text = realtime.skyCon.getSky().info

        "空气指数 ${realtime.airQuality.aqi.chn.toInt()}".let {
            findViewById<TextView>(R.id.currentAQI).text = it
        }

        findViewById<RelativeLayout>(R.id.nowLayout).setBackgroundResource(realtime.skyCon.getSky().bg)

        val forecastLayout = findViewById<LinearLayout>(R.id.forecastLayout).apply {
            removeAllViews()
        }

        // 填充 forecast.xml 中的数据
        val days = daily.skyCon.size
        for (i in 0 until days) {
            val skyCon = daily.skyCon[i]
            val temperature = daily.temperature[i]
            val sky = skyCon.value.getSky()
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                forecastLayout, false).apply {

                findViewById<TextView>(R.id.dateInfo).text =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(skyCon.date)
                findViewById<ImageView>(R.id.skyIcon).setImageResource(sky.icon)
                findViewById<TextView>(R.id.skyInfo).text = sky.info
                "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃".let {
                    findViewById<TextView>(R.id.temperatureInfo).text = it
                }
            }
            forecastLayout.addView(view)
        }

        // 填充 life_index.xml 中的数据
        val lifeIndex = daily.lifeIndex
        findViewById<TextView>(R.id.coldRiskText).text = lifeIndex.coldRisk[0].desc
        findViewById<TextView>(R.id.dressingText).text = lifeIndex.dressing[0].desc
        findViewById<TextView>(R.id.ultravioletText).text = lifeIndex.ultraviolet[0].desc
        findViewById<TextView>(R.id.carWashingText).text = lifeIndex.carWashing[0].desc

        findViewById<ScrollView>(R.id.weatherLayout).visibility = View.VISIBLE
    }
}