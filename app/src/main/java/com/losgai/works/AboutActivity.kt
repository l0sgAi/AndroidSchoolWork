package com.losgai.works

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.losgai.works.helper.DatabaseHelper
import com.losgai.works.ui.theme.MyApplicationTheme

class AboutActivity : ComponentActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var phoneBtn: Button
    private lateinit var emailBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_layout) // 进入关于页面
        databaseHelper = DatabaseHelper(this)
        databaseHelper.insertInitialUserIfEmpty("admin", "123456",this)
        databaseHelper.insertInitialStudentIfEmpty(this)

        phoneBtn = findViewById(R.id.phoneBtn)
        phoneBtn.setOnClickListener {
            makePhoneCall()
        }

        emailBtn = findViewById(R.id.emailBtn)
        emailBtn.setOnClickListener {
            val recipient = "xxxxxxxxx@gmail.com" // 邮箱地址
            val subject = "学生信息" // 主题
            val body = databaseHelper.getAllStudents().joinToString("\n\n") { student ->
                        "学号：${student.stuId}\n" + "姓名: ${student.stuName}\n" +
                        "性别: ${student.sex}\n" + "学院: ${student.institution}\n" +
                        "专业: ${student.major}\n" + "爱好: ${student.hobby}\n" +
                        "生日: ${student.birthYear}-${student.birthMonth}-${student.birhday}\n"}// 正文学生信息

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            startActivity(intent)
        }
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