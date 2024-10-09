package com.losgai.works

import android.app.AlertDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.losgai.works.adapter.StudentAdapter
import com.losgai.works.entity.Student
import com.losgai.works.ui.theme.MyApplicationTheme

//ComponentActivity()
class MainActivity : AppCompatActivity() {
    // 创建3个初始学生对象，加入初始列表
    val student1 = Student(
        R.drawable.user,
        "S3305",
        "张三",
        "男",
        "计算机与通信工程学院",
        "计算机科学与技术",
        "音乐",
        2001,
        0,
        1
    )
    val student2 =
        Student(
            R.drawable.user,
            "S3306",
            "李四",
            "女",
            "计算机与通信工程学院",
            "软件工程",
            "跑步",
            2002,
            0,
            1
        )
    val student3 =
        Student(
            R.drawable.user,
            "S3307",
            "王五",
            "女",
            "电气学院",
            "电机工程",
            "游泳",
            2003,
            0,
            1
        )
    private var students = mutableListOf(student1, student2, student3) // 创建可变学生列表
    private lateinit var listViewStudents: ListView

    //private lateinit var btnAdd: Button
    private val activityContext = this
    private lateinit var adapterStu: StudentAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stu_list_main)

        // 绑定控件
        listViewStudents = findViewById(R.id.listViewStudents)
        // btnAdd = findViewById(R.id.stuAdd) // 初始化 Button

        // 创建适配器并设置给 ListView 定义适配器 控件-桥梁-数据
        adapterStu = StudentAdapter(this, R.layout.inner_list_layout, students)
        listViewStudents.adapter = adapterStu

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_stu)
        setSupportActionBar(toolbar)

        // 设置长按监听器
        listViewStudents.setOnItemLongClickListener { parent, view, position, id ->
            // 获取长按的表项数据
            val itemStu: Student? = adapterStu.getItem(position)
            Log.i("INFO", "stuName: " + itemStu?.stuName + " " + itemStu?.major)
            if (itemStu != null) {
                showDialogOperation(adapterStu, itemStu)
            }
            true
        }

//        btnAdd.setOnClickListener {
//            // 弹出表单页面
//            showDialog(adapterStu)
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // 菜单配合toolbar
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // 菜单点击事件监听
        return when (item.itemId) {
            R.id.menu_stu_add -> {
                showDialog(adapterStu)
                true
            }

            R.id.menu_stu_refresh -> {
                Toast.makeText(this, "刷新", Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
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
        val birthDate: DatePicker = dialogView.findViewById(R.id.birthDateStu)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        var curYear = calendar.get(Calendar.YEAR)
        var curMonth = calendar.get(Calendar.MONTH)
        var curDay = calendar.get(Calendar.DAY_OF_MONTH)

        // 初始化DatePicker并设置日期改变监听器
        birthDate.init(year, month, day) { _, year, monthOfYear, dayOfMonth ->
            // 日期被改变时，监听并赋值
            val selectedDate = "$year-$monthOfYear-$dayOfMonth"
            Log.d("INFO", "Selected date: $selectedDate")
            curYear = year
            curMonth = monthOfYear
            curDay = dayOfMonth
        }
        val buttonSubmit: Button = dialogView.findViewById(R.id.submitId01)

        val dialog = builder.create()
        dialog.show()

        buttonSubmit.setOnClickListener {
            val data = Student(
                R.drawable.user,
                stuId.text.toString(),
                name.text.toString(),
                sex,
                institution.selectedItem.toString(),
                major.selectedItem.toString(),
                hobby.text.toString(),
                curYear,
                curMonth,
                curDay
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

        val delBtn = dialogView.findViewById<Button>(R.id.delete)
        val editBtn = dialogView.findViewById<Button>(R.id.edit)

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
            showEditDialog(adapterStu, itemStu)
            alertDialog.dismiss() // 当操作完成后，关闭对话框
        }

    }

    private fun showEditDialog(adapterStu: StudentAdapter, itemStu: Student) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.activity_main, null)
        builder.setView(dialogView)


        val name: EditText = dialogView.findViewById(R.id.et_name)
        name.setText(itemStu.stuName)

        val stuId: EditText = dialogView.findViewById(R.id.et_student_id)
        stuId.setText(itemStu.stuId)
        stuId.isEnabled = false;//去掉点击时编辑框下面横线:
        stuId.isFocusable = false;//不可编辑
        stuId.isFocusableInTouchMode = false;//不可编辑


        var sex = "null"
        val sexGroup: RadioGroup = dialogView.findViewById(R.id.sexGroup)
        sexGroup.setOnCheckedChangeListener { group, checkedId ->
            // 当RadioButton的选择改变时，这个方法会被调用
            val radioButton: RadioButton = group.findViewById(checkedId)
            sex = radioButton.text.toString()
        }
        // 设置性别默认值
        if (itemStu.sex == "男") {
            dialogView.findViewById<RadioButton>(R.id.sexMan).isChecked = true
        } else if (itemStu.sex == "女") {
            dialogView.findViewById<RadioButton>(R.id.sexWoman).isChecked = true
        }


        // 获取资源文件中的字符串数组
        val institutions = resources.getStringArray(R.array.academy)
        // 学院选项的下拉列表
        val institution: Spinner = dialogView.findViewById(R.id.institutionSpinner)
        // 专业选项的下拉列表
        val major: Spinner = dialogView.findViewById(R.id.majorSpinner)

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

                // 只有创建了onItemSelectedListener之后，默认值才能生效
                institution.setSelection(institutions.indexOf(itemStu.institution))
                if (itemStu.institution == institutions[0]) { // 计算机学院
                    val majors = resources.getStringArray(R.array.cs)
                    major.setSelection(majors.indexOf(itemStu.major))
                } else if (itemStu.institution == institutions[1]) { // 电气学院
                    val majors = resources.getStringArray(R.array.ee)
                    major.setSelection(majors.indexOf(itemStu.major))
                }
            }


            override fun onNothingSelected(parent: AdapterView<*>) {
                // 这里是当没有选项被选中时执行的代码
            }
        }

        val hobby: EditText = dialogView.findViewById(R.id.et_hobby)
        hobby.setText(itemStu.hobby)
        val birthDate: DatePicker = dialogView.findViewById(R.id.birthDateStu)
        val year = itemStu.birthYear
        val month = itemStu.birthMonth
        val day = itemStu.birhday

        var curYear = itemStu.birthYear
        var curMonth = itemStu.birthMonth
        var curDay = itemStu.birhday

        // 初始化DatePicker并设置日期改变监听器
        birthDate.init(year, month, day) { _, year, monthOfYear, dayOfMonth ->
            // 日期被改变时，监听并赋值
            val selectedDate = "$year-$monthOfYear-$dayOfMonth"
            Log.d("INFO", "Selected date: $selectedDate")
            curYear = year
            curMonth = monthOfYear
            curDay = dayOfMonth
        }

        val buttonSubmit: Button = dialogView.findViewById(R.id.submitId01)
        val dialog = builder.create()

        buttonSubmit.setOnClickListener {
            val selectedDate = "$curYear-$curMonth-$curDay"
            Log.d("INFO", "提交的日期date: $selectedDate")
            val data = Student(
                R.drawable.user,
                stuId.text.toString(),
                name.text.toString(),
                sex,
                institution.selectedItem.toString(),
                major.selectedItem.toString(),
                hobby.text.toString(),
                curYear,
                curMonth,
                curDay
            )
            if (data.stuName.isNotEmpty() && data.stuId.isNotEmpty() && data.sex != "null") {
                // 使用filter找到编辑的学生
                val studentToModify = students.firstOrNull { it.stuId == data.stuId }

                // 检查是否找到了学生，并进行修改操作
                studentToModify?.let {
                    // 在这里修改学生对象的属性，例如：
                    it.stuName = data.stuName
                    it.sex = data.sex
                    it.institution = data.institution
                    it.major = data.major
                    it.hobby = data.hobby
                    it.birthYear = data.birthYear
                    it.birthMonth = data.birthMonth
                    it.birhday = data.birhday
                }

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
        dialog.show()
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

