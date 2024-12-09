package com.losgai.works.entity

import java.util.ArrayList
// JSON数据实体，用于解析
data class WeatherResponse(
    val status: String,
    val count: String,
    val info: String,
    val infocode: String,
    val forecasts: ArrayList<Forecast>
)

data class Forecast(
    val city: String,
    val adcode: String,
    val province: String,
    val reporttime: String,
    val casts: ArrayList<Cast>
)

data class Cast(
    val date: String,
    val week: String,
    val dayweather: String,
    val nightweather: String,
    val daytemp: String,
    val nighttemp: String,
    val daywind: String,
    val nightwind: String,
    val daypower: String,
    val nightpower: String,
    val daytemp_float: String,
    val nighttemp_float: String
)