package com.losgai.works

import com.losgai.works.helper.DatabaseHelper
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
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_PASSWORD
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_USERNAME
import com.losgai.works.helper.DatabaseHelper.Companion.TABLE_NAME
import com.losgai.works.ui.theme.MyApplicationTheme
import java.security.GeneralSecurityException
import java.security.MessageDigest

class LoginActivity : ComponentActivity() {
    private lateinit var loginBtn: Button
    private lateinit var aboutBtn: Button
    private lateinit var registerBtn: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout) // 首先进入登录页面

        databaseHelper = DatabaseHelper(this) // 初始化数据库
        databaseHelper.insertInitialUserIfEmpty("admin", "123456", this)

        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)

        loginBtn = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (authenticateUser(username, password)) {
                customToast("登录成功", R.layout.toast_view)
                val intent = Intent(this, LoadingActivity::class.java)
                startActivity(intent)
            } else {
                customToast("登录失败，请检查用户名和密码", R.layout.toast_view_e)
            }
        }

        registerBtn = findViewById(R.id.registerBtn)
        registerBtn.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if(registerUser(username, password)){
                customToast("注册成功", R.layout.toast_view)
            } else {
                customToast("注册失败，请检查用户名和密码", R.layout.toast_view_e)
            }
        }

        aboutBtn = findViewById(R.id.aboutBtn)
        aboutBtn.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
    }

    private fun authenticateUser(username: String, password: String): Boolean {
        val encryptedPassword = encryptPassword(password)
        val db = databaseHelper.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_USERNAME =? AND $COLUMN_PASSWORD =?"
        val cursor = db.rawQuery(query, arrayOf(username, encryptedPassword))
        val result = cursor.count > 0
        cursor.close()
        return result
    }

    private fun registerUser(username: String, password: String): Boolean {
        val dbRead = databaseHelper.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_USERNAME =?"
        val cursor = dbRead.rawQuery(query, arrayOf(username))
        val result = cursor.count > 0
        cursor.close()

        if(result){ // 不能注册相同用户名
            customToast("用户名冲突", R.layout.toast_view_e)
            return false
        }

        val dbWrite = databaseHelper.writableDatabase
        if(username.isNotEmpty() && password.isNotEmpty()){
            val encryptedPassword = encryptPassword(password)
            Log.i("INFO：", encryptedPassword)
            val insertQuery = "INSERT INTO $TABLE_NAME ($COLUMN_USERNAME, $COLUMN_PASSWORD) VALUES (?,?)"
            val insertStmt = dbWrite.compileStatement(insertQuery)
            insertStmt.bindString(1, username)
            insertStmt.bindString(2, encryptedPassword)
            insertStmt.executeInsert()
            Log.i("INFO", "用户已添加")
            return true
        }

        return false
    }

    private fun customToast(textInput: String, background: Int) {
        val inflater = layoutInflater
        val layout: View =
            inflater.inflate(background, findViewById(R.id.toast_image))
        // 设置图片和文本
        val text = layout.findViewById<TextView>(R.id.toast_text)
        text.text = textInput
        // 创建Toast并设置自定义布局
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show() // 提示信息
    }

    private fun encryptPassword(password: String): String { //SHA-256加密密码
        try {
            val digest = MessageDigest.getInstance("SHA-256")
            val result = digest.digest(password.toByteArray())
            val stringBuilder = StringBuilder()

            //转成16进制
            result.forEach {
                val value = it
                val hex = value.toInt() and (0xFF)
                val hexStr = Integer.toHexString(hex)
                println(hexStr)
                if(hexStr.length == 1){
                    stringBuilder.append(0).append(hexStr)
                } else {
                    stringBuilder.append(hexStr)
                }
            }

            return stringBuilder.toString()
        } catch (e: GeneralSecurityException) {
            Log.e("INFO", "加密失败: ${e.message}")
            return password
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