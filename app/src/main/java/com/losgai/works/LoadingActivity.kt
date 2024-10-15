package com.losgai.works

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.losgai.works.ui.theme.MyApplicationTheme

class LoadingActivity : ComponentActivity() {
    private lateinit var skipButton: Button
    private var countdown = 5
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ad_temp) // 首先进入加载页面
        // 加载5s后，进入主页面
        skipButton = findViewById(R.id.skip_button)
        skipButton.setOnClickListener {
            // 跳过广告
            navigateToNextScreen()
        }
        // 开始倒计时
        startCountdown()
    }

    private fun navigateToNextScreen() {
        // 停止倒计时
        handler.removeCallbacksAndMessages(null)

        // 跳转到下一个页面，这里假设下一个页面是MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startCountdown() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                countdown--
                if (countdown >= 0) {
                    skipButton.text = "跳过 $countdown"
                    handler.postDelayed(this, 1000)
                } else {
                    navigateToNextScreen()
                }
            }
        }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 确保在Activity销毁时移除所有消息
        handler.removeCallbacksAndMessages(null)
    }

}

@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    MyApplicationTheme {
        Greeting3("Android")
    }
}