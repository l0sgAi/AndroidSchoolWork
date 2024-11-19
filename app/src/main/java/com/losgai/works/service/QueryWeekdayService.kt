package com.losgai.works.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class QueryWeekdayService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("服务状态", "服务启动")
        val currentDate = LocalDate.now()
        val year = currentDate.year
        val month = currentDate.monthValue
        val day = currentDate.dayOfMonth
        val date = LocalDate.of(year, month, day)
        val formatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
        val dayName = date.format(formatter)

        val resultIntent = Intent().setAction("com.losgai.works.service.WEEKDAY") // 返回结果的Action
        resultIntent.putExtra("day_of_week", dayName)
        sendBroadcast(resultIntent)
        return START_STICKY
    }
}