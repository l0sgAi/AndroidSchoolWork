package com.losgai.works

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.losgai.works.dao.StuBaseInfoDao
import com.losgai.works.helper.DatabaseHelper
import com.losgai.works.ui.theme.MyApplicationTheme

class AboutActivity : ComponentActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var phoneBtn: Button
    private lateinit var emailBtn: Button
    private lateinit var saveAboutTextBtn: Button
    private lateinit var stuInfoDao: StuBaseInfoDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_layout) // 进入关于页面
        databaseHelper = DatabaseHelper(this)
        databaseHelper.insertInitialUserIfEmpty("admin", "123456", this)
        databaseHelper.insertInitialStudentIfEmpty(this)
        stuInfoDao = StuBaseInfoDao()

        // 尝试填入保存数据
        val aboutText = findViewById<EditText>(R.id.aboutIntro)
        val sharedPreferences = getSharedPreferences("about", Context.MODE_PRIVATE)
        val savedText = sharedPreferences.getString("about", "")
        aboutText.setText(savedText)

        saveAboutTextBtn = findViewById(R.id.saveTextBtn)
        saveAboutTextBtn.setOnClickListener {
            saveAboutText(sharedPreferences)
        }

        phoneBtn = findViewById(R.id.phoneBtn)
        phoneBtn.setOnClickListener {
            makePhoneCall()
        }

        emailBtn = findViewById(R.id.emailBtn)
        emailBtn.setOnClickListener {
            val recipient = "xxxxxxxxx@gmail.com" // 邮箱地址
            val subject = "学生基本信息" // 主题
            val body = stuInfoDao.getAllStudents(databaseHelper).joinToString("\n\n") { student ->
                "学号：${student.stuId}\n" + "姓名: ${student.stuName}\n" +
                        "性别: ${student.sex}\n" + "学院: ${student.institution}\n" +
                        "专业: ${student.major}\n" + "爱好: ${student.hobby}\n" +
                        "生日: ${student.birthYear}-${student.birthMonth}-${student.birhday}\n" +
                        "个人简介: ${student.intro}\n" + "左右铭: ${student.motto}\n"
            } // 正文学生信息

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            startActivity(intent)
        }
    }

    private fun saveAboutText(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit()
            .putString("about", findViewById<EditText>(R.id.aboutIntro).text.toString()).apply()
        customToast("保存简介成功", R.layout.toast_view)
    }

    private fun makePhoneCall() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                1
            )
        } else {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:188xxxx8888")
            startActivity(callIntent)
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            makePhoneCall()
        }
    }
}

@Composable
fun Greeting4(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    MyApplicationTheme {
        Greeting4("Android")
    }
}