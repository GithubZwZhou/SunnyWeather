package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * 项目名称: SunnyWeather
 * 创建时间: 2021/4/27
 * 描述信息: 未来几天的天气信息，使用 List 集合来映射。
 * @author <a href="mail to: 10185101124@stu.ecnu.edu.cn" rel="nofollow">周政伟</a>
 * @update [1][2021-04-27 19:42] <周政伟><创建>
 */
data class DailyResponse(val status: String, val result: Result) {
    data class Result(val daily: Daily)

    data class Daily(val temperature: List<Temperature>,
                     @SerializedName("skycon") val skyCon: List<SkyCon>,
                     @SerializedName("life_index") val lifeIndex: LifeIndex)

    data class Temperature(val max: Float, val min: Float)

    data class SkyCon(val value: String, val date: Date)

    data class LifeIndex(val coldRisk: List<LifeDescription>,
                         val carWashing: List<LifeDescription>,
                         val ultraviolet: List<LifeDescription>,
                         val dressing: List<LifeDescription>)

    data class LifeDescription(val desc: String)
}
