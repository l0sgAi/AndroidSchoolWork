package com.losgai.works

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.ListView
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.gson.Gson
import com.losgai.works.adapter.WeatherMessageAdapter
import com.losgai.works.entity.WeatherMessage
import com.losgai.works.entity.WeatherResponse
import com.losgai.works.ui.theme.MyApplicationTheme
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class WeatheActivity : ComponentActivity() {

    private var weathers: MutableList<WeatherMessage> = mutableListOf()
    private lateinit var jsonWeatherDataStr: String
    private lateinit var listViewWeather: ListView
    private lateinit var adapterWeather: WeatherMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_layout)
        val policy = StrictMode.ThreadPolicy.Builder()
            .permitNetwork().build()
        StrictMode.setThreadPolicy(policy)
        // 绑定控件
        listViewWeather = findViewById(R.id.listViewWeather)
        // 创建适配器并设置给 ListView 定义适配器 控件-桥梁-数据
        adapterWeather = WeatherMessageAdapter(this, R.layout.inner_weather_layout, weathers)
        listViewWeather.adapter = adapterWeather
        // TODO: 使用自己的高德key
        jsonWeatherDataStr = getUrlData("https://restapi.amap.com/v3/weather/weatherInfo?key=【你的高德key】&city=321100&extensions=all")
        Log.i("WeatherActivity", jsonWeatherDataStr)
        val gson = Gson()
        val jsonWeatherData: WeatherResponse = gson.fromJson(jsonWeatherDataStr, WeatherResponse::class.java)
        val weatherData = jsonWeatherData.forecasts[0].casts
        for (cast in weatherData) {
            val weather = WeatherMessage(
                when (cast.dayweather) {
                    "晴" -> R.drawable.day
                    "多云" -> R.drawable.cloudy
                    "阴" -> R.drawable.dark
                    "小雨" -> R.drawable.small_rainy
                    "中雨" -> R.drawable.medium_rainy
                    "大雨" -> R.drawable.big_rainy
                    "雷阵雨" -> R.drawable.thunder
                    else -> R.drawable.question
                },

                location = "中国 江苏省 镇江市 京口区",

                date = cast.date + when(cast.week){
                    "1"->" 周一"
                    "2"->" 周二"
                    "3"->" 周三"
                    "4"->" 周四"
                    "5"->" 周五"
                    "6"->" 周六"
                    "7"->" 周日"
                    else -> "未知"
                },

                temperature = cast.daytemp + "°C",
                weather = cast.dayweather,
                wind = cast.daywind + "风, " + cast.daypower + "级"
            )
            weathers.add(weather)
        }

//////////控制台测试
//        for(w in weathers){
//            Log.i("初始化",w.date)
//        }

    }

    private fun getUrlData(urlInput : String) : String {
        var result = ""
        try {
            val url = URL(urlInput)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val buffer = StringBuffer()
            var line: String? = reader.readLine()
            while (line!= null) {
                buffer.append(line)
                line = reader.readLine()
            }
            result = buffer.toString()
            reader.close()
            inputStream.close()
            connection.disconnect()
        } catch (e: IOException) {
            Log.e("WeatherAPITask", "Error in API call", e)
        }

        return result
    }
}

@Composable
fun Greeting5(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview5() {
    MyApplicationTheme {
        Greeting5("Android")
    }
}