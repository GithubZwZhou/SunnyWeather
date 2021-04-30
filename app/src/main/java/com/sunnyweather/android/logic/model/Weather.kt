package com.sunnyweather.android.logic.model

/**
 * 项目名称: SunnyWeather
 * 创建时间: 2021/4/27
 * 描述信息: 封装 Realtime 和 Daily 对象
 * @author <a href="mail to: 10185101124@stu.ecnu.edu.cn" rel="nofollow">周政伟</a>
 * @update [1][2021-04-27 21:05] <周政伟><创建>
 */
data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily)
