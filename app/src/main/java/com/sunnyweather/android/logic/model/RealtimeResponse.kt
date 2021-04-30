package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

/**
 * 项目名称: SunnyWeather
 * 创建时间: 2021/4/27
 * 描述信息: 实时天气信息数据模型，把所有模型类定义在内部可以防止同名冲突
 * @author <a href="mail to: 10185101124@stu.ecnu.edu.cn" rel="nofollow">周政伟</a>
 * @update [1][2021-04-27 19:33] <周政伟><创建>
 */
data class RealtimeResponse(val status: String, val result: Result) {
    data class Result(val realtime: Realtime)

    data class Realtime(@SerializedName("skycon") val skyCon: String,
                        val temperature: Float,
                        @SerializedName("air_quality") val airQuality: AirQuality)

    data class AirQuality(val aqi: AQI)

    data class AQI(val chn: Float)
}
