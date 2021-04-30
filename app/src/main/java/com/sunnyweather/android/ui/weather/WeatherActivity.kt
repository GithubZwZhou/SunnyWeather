package com.sunnyweather.android.ui.weather

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import com.sunnyweather.android.ui.place.WeatherViewModel
import com.sunnyweather.android.util.makeDateString
import com.sunnyweather.android.util.makeToast

class WeatherActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "WeatherActivity"
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }

    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_weather)

        swipeRefresh = findViewById(R.id.swipeRefresh)

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
                "刷新成功".makeToast(this)
            } else {
                "无法获取天气信息".makeToast(this)
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false

        }
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
//        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        findViewById<Button>(R.id.navButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object: DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            /**
             * 隐藏输入法
             */
            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })
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

                findViewById<TextView>(R.id.dateInfo).text = skyCon.date.makeDateString()
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

    private fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }

    /**
     * 用于外部更新城市信息
     */
    fun updateWeather(lng: String, lat: String, name: String) {
        viewModel.locationLng = lng
        viewModel.locationLat = lat
        viewModel.placeName = name
        refreshWeather()
    }
}