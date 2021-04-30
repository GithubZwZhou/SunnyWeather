package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * 项目名称: SunnyWeather
 * 创建时间: 2021/4/24
 * 描述信息: 为项目提供一种全局获取 Context 的方式
 * @author <a href="mail to: 10185101124@stu.ecnu.edu.cn" rel="nofollow">周政伟</a>
 * @update [1][2021-04-24 20:21] <周政伟><创建>
 */
class SunnyWeatherApplication: Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        const val TOKEN = "8TZr9i8eMmZIezLO" // 彩云天气的令牌值
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}