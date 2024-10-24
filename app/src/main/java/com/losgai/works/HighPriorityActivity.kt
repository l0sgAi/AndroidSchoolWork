package com.losgai.works

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.losgai.works.ui.theme.MyApplicationTheme
import kotlin.random.Random

class HighPriorityActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)
//        val largeArray = ByteArray(1024 * 1024 * 62) // 分配50MB的字节数组
//        val random = Random(2024)
//        random.nextBytes(largeArray)
//
//        val largeArray1 = ByteArray(1024 * 1024 * 62) // 分配50MB的字节数组
//        val random1 = Random(2024)
//        random1.nextBytes(largeArray1)
//
//        val largeArray2 = ByteArray(1024 * 1024 * 62) // 分配50MB的字节数组
//        val random2 = Random(2024)
//        random2.nextBytes(largeArray2)
//
//        val startTime = System.currentTimeMillis()
//        var sum = 0L
//        while (System.currentTimeMillis() - startTime < 10000) {
//            for (i in 1..1000000) {
//                sum += i * i
//            }
//        }
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