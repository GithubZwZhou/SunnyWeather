package com.sunnyweather.android.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import com.sunnyweather.android.util.makeToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

/**
 * 项目名称: SunnyWeather
 * 创建时间: 2021/4/24
 * 描述信息: 仓库层的统一封装入口，定义了一些能将异步获取的数据以响应式编程
 *  (LiveData)的方式通知给上一层 的方法。
 * @author <a href="mail to: 10185101124@stu.ecnu.edu.cn" rel="nofollow">周政伟</a>
 * @update [1][2021-04-24 21:09] <周政伟><创建>
 */
object Repository {
    private const val TAG = "Repository"

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        // 并发以提高效率
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()

            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime,
                        dailyResponse.result.daily)
                Log.d(TAG, "$lng + $lat + ${weather.toString()}")
                Result.success(weather)
            } else {
                Result.failure(
                        RuntimeException(
                                "realtime response status is ${realtimeResponse.status}" +
                                        "daily response status is ${dailyResponse.status}"
                        )
                )
            }
        }
    }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    /**
     * @param context: 一个挂起函数的上下文.
     *  -- 指定为 Dispatcher.IO 使得代码块在子线程中运行。
     * @param block: 包含挂起函数的代码块
     */
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        // 自动构建并返回一个 LiveData 对象
        liveData(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result) // 发射包装的结果
        }
}