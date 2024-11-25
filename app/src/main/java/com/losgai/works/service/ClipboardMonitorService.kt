package com.losgai.works.service

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi

class ClipboardMonitorService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private val clipboardManager: ClipboardManager by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("剪切板服务状态", "服务启动")
        val clipData = clipboardManager.primaryClip
        if (clipData != null)
            Log.i("学号填充服务", "剪贴板内容${clipData.getItemAt(0).text}")

        if (clipData!= null && clipData.itemCount > 0) {
            val clip = clipData.getItemAt(0).text.toString()
            val pattern = Regex("^SE\\d{6}$")
            if (pattern.matches(clip)) {
                val resultIntent = Intent().setAction("com.losgai.works.service.HAS_STUDENT_RECORD")
                resultIntent.putExtra("studentId", clip)
                sendBroadcast(resultIntent)
                Log.i("ClipboardMonitorService", "检测到学生记录，已发送广播，学号为: $clip")
            } else{
                Log.i("ClipboardMonitorService", "剪贴板内容$clip 不匹配")
            }
        }
        return START_STICKY
    }

}