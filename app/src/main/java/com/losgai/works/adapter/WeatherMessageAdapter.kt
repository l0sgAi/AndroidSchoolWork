package com.losgai.works.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.losgai.works.R
import com.losgai.works.entity.Student
import com.losgai.works.entity.WeatherMessage

class WeatherMessageAdapter(context: Context, resource: Int, objects: MutableList<WeatherMessage>) :
    ArrayAdapter<WeatherMessage>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val weatherMessage = getItem(position) ?: return convertView ?: View(context)
        val view = LayoutInflater.from(context).inflate(R.layout.inner_weather_layout, parent, false)

        val weatherImage = view.findViewById<ImageView>(R.id.imageUrlWeather)
        val location = view.findViewById<TextView>(R.id.location)
        val weatherDate = view.findViewById<TextView>(R.id.date)
        val temperature = view.findViewById<TextView>(R.id.temperature)
        val weather = view.findViewById<TextView>(R.id.weather)
        val wind = view.findViewById<TextView>(R.id.wind)

        // Set the image and text for the views
        weatherImage.setImageResource(weatherMessage.imageUrl)
        location.text = "位置： " + weatherMessage.location
        weatherDate.text = "日期： " + weatherMessage.date
        temperature.text = "温度： " + weatherMessage.temperature
        weather.text = "天气： " + weatherMessage.weather
        wind.text = "风力： " + weatherMessage.wind


        return view
    }
}

