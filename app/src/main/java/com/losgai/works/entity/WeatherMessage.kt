package com.losgai.works.entity

import java.util.Date

class WeatherMessage(
    var imageUrl: Int,
    var location : String,
    val date: String,
    var temperature: String,
    var weather: String,
    var wind: String,
)  {
}