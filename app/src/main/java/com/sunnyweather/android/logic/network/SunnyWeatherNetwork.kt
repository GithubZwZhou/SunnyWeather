package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 项目名称: SunnyWeather
 * 创建时间: 2021/4/24
 * 描述信息: 对所有网络请求 API 进行封装的同一网络数据源访问入口。
 * @author <a href="mail to: 10185101124@stu.ecnu.edu.cn" rel="nofollow">周政伟</a>
 * @update [1][2021-04-24 20:55] <周政伟><创建>
 */
object SunnyWeatherNetwork {
    // 接口的动态代理对象
    private val placeService = ServiceCreator.create<PlaceService>()
    private val weatherService = ServiceCreator.create<WeatherService>()

    // 发起搜索城市数据请求的 挂起函数
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    suspend fun getDailyWeather(lng: String, lat: String) =
            weatherService.getDailyWeather(lng, lat).await()

    suspend fun getRealtimeWeather(lng: String, lat: String) =
            weatherService.getRealtimeWeather(lng, lat).await()

    /**
     * 借助协程技术，将解析出来的数据模型对象取出并返回
     */
    private suspend fun <T> Call<T>.await() = suspendCoroutine<T> { continuation ->
        enqueue(object: Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                if (body != null)
                    continuation.resume(body)
                else continuation.resumeWithException(
                    RuntimeException("response body is null")
                )
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}