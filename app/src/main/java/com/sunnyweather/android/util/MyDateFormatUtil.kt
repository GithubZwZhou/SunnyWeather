package com.sunnyweather.android.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * 项目名称: SunnyWeather
 * 创建时间: 2021/4/30
 * 描述信息: 用于构造 Date 信息。
 * @author <a href="mail to: 10185101124@stu.ecnu.edu.cn" rel="nofollow">周政伟</a>
 * @update [1][2021-04-30 19:11] <周政伟><创建>
 */
fun Date.makeDateString() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    .format(this)