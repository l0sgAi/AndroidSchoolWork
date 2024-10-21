package com.losgai.works

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.losgai.works.ui.theme.MyApplicationTheme

class LoginActivity : ComponentActivity() {
    private lateinit var loginBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("TEST", "生命周期 onCreate() 调用")
        setContentView(R.layout.login_layout) // 首先进入登录页面
        loginBtn = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener {
            val username = findViewById<EditText>(R.id.username).text.toString()
            val password = findViewById<EditText>(R.id.password).text.toString()
            if(username == "admin"){
                if(password == "123456"){
                    // 跳转加载页面
                    val intent = Intent(this, LoadingActivity::class.java)
                    startActivity(intent)
                }
                else{
                    val inflater = layoutInflater
                    val layout: View =
                        inflater.inflate(R.layout.toast_view_e, findViewById(R.id.toast_image))
                    // 设置图片和文本
                    val text = layout.findViewById<TextView>(R.id.toast_text)
                    text.text = "密码错误"
                    // 创建Toast并设置自定义布局
                    val toast = Toast(applicationContext)
                    toast.duration = Toast.LENGTH_SHORT
                    toast.view = layout
                    toast.show() // 提示信息
                }
            }else{
                val inflater = layoutInflater
                val layout: View =
                    inflater.inflate(R.layout.toast_view_e, findViewById(R.id.toast_image))
                // 设置图片和文本
                val text = layout.findViewById<TextView>(R.id.toast_text)
                text.text = "用户不存在"
                // 创建Toast并设置自定义布局
                val toast = Toast(applicationContext)
                toast.duration = Toast.LENGTH_SHORT
                toast.view = layout
                toast.show() // 提示信息
            }
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MyApplicationTheme {
        Greeting2("Android")
    }
}