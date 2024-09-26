package com.losgai.works

import android.app.AlertDialog
import android.app.AlertDialog.Builder
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.losgai.works.adapter.StudentAdapter
import com.losgai.works.entity.Student
import com.losgai.works.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private var students = mutableListOf<Student>() // 创建可变学生列表
    private lateinit var listViewStudents: ListView
    private lateinit var btnAdd: Button
    private val activityContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stu_list_main)
        // 创建3个初始学生对象，加入初始列表
        val student1 = Student(
            R.drawable.user,
            "S3305",
            "张三",
            "男",
            "计算机学院",
            "计算机科学与技术",
            "音乐"
        )
        val student2 =
            Student(
                R.drawable.user,
                "S3306",
                "李四",
                "女",
                "计算机学院",
                "软件工程",
                "跑步"
            )
        val student3 =
            Student(
                R.drawable.user,
                "S3307",
                "王五",
                "女",
                "电气学院",
                "电机工程",
                "游泳"
            )
        students = mutableListOf(student1, student2, student3)

        // 绑定控件
        listViewStudents = findViewById(R.id.listViewStudents)
        btnAdd = findViewById(R.id.btnAdd) // 初始化 Button

        // 创建适配器并设置给 ListView 定义适配器 控件-桥梁-数据
        var adapterStu = StudentAdapter(this, R.layout.inner_list_layout, students)
        listViewStudents.adapter = adapterStu

        // 设置长按监听器
        listViewStudents.setOnItemLongClickListener { parent, view, position, id ->
            // 获取长按的表项数据
            val itemStu: Student? = adapterStu.getItem(position)
            Log.i("INFO", "stuName: " + itemStu?.stuName)
            if (itemStu != null) {
                showDialogOperation(adapterStu, itemStu)
            }
            true
        }

        btnAdd.setOnClickListener {
            // 弹出表单页面
            showDialog(adapterStu)
        }
    }

    private fun showDialog(adapterStu: StudentAdapter) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.activity_main, null)
        builder.setView(dialogView)


        val name: EditText = dialogView.findViewById(R.id.et_name)
        val stuId: EditText = dialogView.findViewById(R.id.et_student_id)
        var sex = "null"
        val sexGroup: RadioGroup = dialogView.findViewById(R.id.sexGroup)
        sexGroup.setOnCheckedChangeListener { group, checkedId ->
            // 当RadioButton的选择改变时，这个方法会被调用
            val radioButton: RadioButton = group.findViewById(checkedId)
            sex = radioButton.text.toString()
        }

        // 专业选项的下拉列表
        val major: Spinner = dialogView.findViewById(R.id.majorSpinner)

        // 学院选项的下拉列表
        val institution: Spinner = dialogView.findViewById(R.id.institutionSpinner)
        // 获取资源文件中的字符串数组
        val institutions = resources.getStringArray(R.array.academy)
        // 创建ArrayAdapter，并指定布局（这里使用的是默认的simple_spinner_item布局）
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, institutions)
        // 指定下拉列表的布局样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // 将适配器设置到Spinner上
        institution.adapter = adapter

        // 创建一个OnItemSelectedListener
        institution.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 这里是当选项被选中时执行的代码
                val selectedItem = parent.getItemAtPosition(position) as String // 适配器返回的是String类型
                Log.d("INFO", "Selected item: $selectedItem")

                if (selectedItem == "计算机与通信工程学院") {
                    // 获取资源文件中的字符串数组
                    val strArr = resources.getStringArray(R.array.cs)
                    // 创建ArrayAdapter，并指定布局
                    val adapter01 =
                        ArrayAdapter(activityContext, android.R.layout.simple_spinner_item, strArr)
                    // 指定下拉列表的布局样式
                    adapter01.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // 将适配器设置到Spinner上
                    major.adapter = adapter01
                } else if (selectedItem == "电气学院") {
                    // 获取资源文件中的字符串数组
                    val strArr = resources.getStringArray(R.array.ee)
                    // 创建ArrayAdapter，并指定布局
                    val adapter01 =
                        ArrayAdapter(activityContext, android.R.layout.simple_spinner_item, strArr)
                    // 指定下拉列表的布局样式
                    adapter01.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // 将适配器设置到Spinner上
                    major.adapter = adapter01
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 这里是当没有选项被选中时执行的代码
            }
        }

        val hobby: EditText = dialogView.findViewById(R.id.et_hobby)
        val buttonSubmit: Button = dialogView.findViewById(R.id.submitId01)

        val dialog = builder.create()
        dialog.show()

        buttonSubmit.setOnClickListener {
            val data: Student = Student(
                R.drawable.user,
                stuId.text.toString(),
                name.text.toString(),
                sex,
                institution.selectedItem.toString(),
                major.selectedItem.toString(),
                hobby.text.toString()
            )
            if (data.stuName.isNotEmpty() && data.stuId.isNotEmpty() && data.sex != "null") {
                // 将新学生对象添加到列表
                students.add(data)
                // 通知适配器数据已改变
                adapterStu.notifyDataSetChanged()

                val inflater = layoutInflater
                val layout: View =
                    inflater.inflate(R.layout.toast_view, findViewById(R.id.toast_image))
                // 设置图片和文本
                val text = layout.findViewById<TextView>(R.id.toast_text)
                text.text = "数据已提交"
                // 创建Toast并设置自定义布局
                val toast = Toast(applicationContext)
                toast.duration = Toast.LENGTH_SHORT
                toast.view = layout
                toast.show() // 提示信息
                dialog.dismiss()
            } else {
                val inflater = layoutInflater
                val layout: View =
                    inflater.inflate(R.layout.toast_view_e, findViewById(R.id.toast_image))

                // 设置图片和文本
                val text = layout.findViewById<TextView>(R.id.toast_text)
                text.text = "提交数据失败，至少输入姓名、性别和学号！"

                // 创建Toast并设置自定义布局
                val toast = Toast(applicationContext)
                toast.duration = Toast.LENGTH_SHORT
                toast.view = layout
                toast.show() // 提示信息
            }
        }
    }

    private fun showDialogOperation(adapterStu: StudentAdapter, itemStu: Student) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.opera_select, null)
        builder.setView(dialogView)
        Log.i("INFO", "showDialogOperation")

        val delBtn =  dialogView.findViewById<Button>(R.id.delete)
        val editBtn =  dialogView.findViewById<Button>(R.id.edit)

        // 创建并显示对话框
        val alertDialog = builder.create()
        alertDialog.show()

        delBtn.setOnClickListener() {
            // 处理删除操作
            // 创建并显示确认对话框
            val confirmBuilder = AlertDialog.Builder(this)
            confirmBuilder.setMessage("确认删除该学生信息？")
                .setPositiveButton("确认") { _, _ ->
                    // 用户点击确认后，执行删除操作
                    // 使用filter函数过滤出不包含特定stuId的Student对象
                    val updatedStudents = students.filter { it.stuId != itemStu.stuId }
                    // 清空原列表并添加过滤后的学生列表
                    students.clear()
                    students.addAll(updatedStudents)
                    // 通知适配器数据已改变
                    adapterStu.notifyDataSetChanged()
                    alertDialog.dismiss() // 关闭操作选择对话框
                }
                .setNegativeButton("取消", null) // 用户点击取消，不做任何操作
            confirmBuilder.create().show()
            alertDialog.dismiss() // 当操作完成后，关闭对话框
        }

        editBtn.setOnClickListener() {
            // 处理修改操作
            alertDialog.dismiss() // 当操作完成后，关闭对话框
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}

