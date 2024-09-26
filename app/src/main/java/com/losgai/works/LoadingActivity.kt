package com.losgai.works

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.losgai.works.ui.theme.MyApplicationTheme

class LoadingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.process_circle) // 首先进入加载页面
        // TODO:加载3s后，进入主页面
        Handler(Looper.getMainLooper()).postDelayed({
            // 延迟 3 秒后执行
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 销毁当前 Activity，避免用户按返回键回到加载页
        }, 3000) // 3000 毫秒 = 3 秒
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