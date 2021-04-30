package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

/**
 * 按照搜索城市数据接口返回的 JSON 格式定义的数据模型。
 */
data class PlaceResponse(val status: String, val places: List<Place>)
/**
 * 使用 @SerializedName 让 JSON字段和 Kotlin字段之间建立映射关系。
 */
data class Place(val name: String, val location: Location,
                 @SerializedName("formatted_address") val address: String)
data class Location(val lng: String, val lat: String)