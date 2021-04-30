package com.sunnyweather.android.util

import android.content.Context
import android.widget.Toast

fun String.makeToast(context: Context?, duration: Int = Toast.LENGTH_SHORT) {
    context?.let { Toast.makeText(context, this,  duration).show() }
}