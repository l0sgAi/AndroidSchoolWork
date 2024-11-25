package com.losgai.works

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.icu.util.Calendar
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.losgai.works.adapter.StudentAdapter
import com.losgai.works.dao.StuBaseInfoDao
import com.losgai.works.dao.StuExtInfoDao
import com.losgai.works.entity.Student
import com.losgai.works.helper.DatabaseHelper
import com.losgai.works.ui.theme.MyApplicationTheme

class MainActivity : AppCompatActivity() {
    private lateinit var listViewStudents: ListView
    private val activityContext = this
    private lateinit var adapterStu: StudentAdapter
    private lateinit var filteredStu: StudentAdapter // 这个适配器用于过滤后的学生列表
    private var students: MutableList<Student> = mutableListOf()
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var stuInfoDao: StuBaseInfoDao
    private lateinit var stuExtInfoDao: StuExtInfoDao

    private val studentRecordBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val studentId = intent.getStringExtra("studentId")
            if (studentId != null) {
                try {
                    Log.i("StudentRecordReceiver", "接收到学生记录广播，学号为: $studentId")
                    showDialog(adapterStu, studentId)
                } catch (e: Exception) {
                    Log.e("学号读取服务错误", e.message.toString())
                }
            } else {
                customToast("未检测读取到学生学号", R.layout.toast_view_e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.stu_list_main)

        databaseHelper = DatabaseHelper(this) // 初始化数据库
        databaseHelper.insertInitialStudentIfEmpty(this) // 插入初始数据
        stuInfoDao = StuBaseInfoDao()
        stuExtInfoDao = StuExtInfoDao()
        students = stuInfoDao.getAllStudents(databaseHelper)// 创建可变学生列表

        // 绑定控件
        listViewStudents = findViewById(R.id.listViewStudents)

        // 创建适配器并设置给 ListView 定义适配器 控件-桥梁-数据
        adapterStu = StudentAdapter(this, R.layout.inner_list_layout, students)
        listViewStudents.adapter = adapterStu

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_stu)
        setSupportActionBar(toolbar)

        // 注册监听器
        registerReceiver(
            studentRecordBroadcastReceiver,
            IntentFilter("com.losgai.works.service.HAS_STUDENT_RECORD"),
            RECEIVER_EXPORTED
        )

        // 启动服务
        val intent = Intent().setClassName(
            "com.losgai.works",
            "com.losgai.works.service.ClipboardMonitorService"
        )
        startService(intent)

        // 设置长按监听器
        listViewStudents.setOnItemLongClickListener { parent, view, position, id ->
            // 获取长按的表项数据
            when (listViewStudents.adapter) {
                adapterStu -> { // 非查询状态
                    val itemStu: Student? = adapterStu.getItem(position)
                    Log.i("INFO", "stuName: " + itemStu?.stuName + " " + itemStu?.major)
                    if (itemStu != null)
                        showDialogOperation(adapterStu, itemStu)
                    true
                }

                filteredStu -> { // 查询状态
                    val itemStu: Student? = filteredStu.getItem(position)
                    Log.i("INFO", "stuName: " + itemStu?.stuName + " " + itemStu?.major)
                    if (itemStu != null)
                        showDialogOperation(filteredStu, itemStu)
                    true
                }

                else -> false
            }
        }
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

            R.id.menu_stu_search -> {
                showDialogSearch(adapterStu)
                true
            }

            R.id.menu_stu_refresh -> {
                reset("已刷新数据")
                true
            }

            R.id.menu_stu_saveToFile -> {
                saveTofile()
                true
            }

            R.id.menu_stu_import -> {
                importData()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveTofile() {
        val sharedPreferences = this.getSharedPreferences("student", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if (students.isEmpty()) {
            customToast("没有数据", R.layout.toast_view_e)
        } else {
            val studentsString = students.joinToString(".") { student ->
                "${student.stuId},${student.stuName}," +
                        "${student.sex},${student.institution}," +
                        "${student.major},${student.hobby}," +
                        "${student.birthYear},${student.birthMonth},${student.birhday}," +
                        "${student.intro},${student.motto}"
            }
            // 所有学生信息转为字符串
            editor.putString("studentList", studentsString)
            editor.apply()
            customToast("保存当前数据成功", R.layout.toast_view)
        }
    }

    private fun importData() {
        val sharedPreferences = this.getSharedPreferences("student", Context.MODE_PRIVATE)
        val studentsString = sharedPreferences.getString("studentList", "")!! // !!强制转换为非空
        val studentList = mutableListOf<Student>()
        val studentDataArray = studentsString.split('.')
        Log.i("学生信息字符串： ", studentsString)

        for (studentData in studentDataArray) {
            val parts = studentData.split(',')
            Log.i("当前部分： ", parts.size.toString())
            if (parts.size == 11) {
                val stuId = parts[0]
                val stuName = parts[1]
                val sex = parts[2]
                val institution = parts[3]
                val major = parts[4]
                val hobby = parts[5]
                val birthYear = parts[6].toInt()
                val birthMonth = parts[7].toInt()
                val birthday = parts[8].toInt()
                val intro = parts[9]
                val motto = parts[10]
                studentList.add(
                    Student(
                        R.drawable.user,
                        stuId,
                        stuName,
                        sex,
                        institution,
                        major,
                        hobby,
                        birthYear,
                        birthMonth,
                        birthday,
                        intro,
                        motto
                    )
                )
            }
        }

        if (studentList.isNotEmpty()) {
            databaseHelper.loadStudent(studentList)
            listViewStudents.adapter = adapterStu
            students.clear()
            students.addAll(stuInfoDao.getAllStudents(databaseHelper))
            adapterStu.notifyDataSetChanged()
            customToast("已读取之前保存的数据", R.layout.toast_view)
        } else {
            customToast("没有读取到之前保存的数据", R.layout.toast_view_e)
        }
    }

    private fun reset(msg: String, isShow: Boolean = true) {
        listViewStudents.adapter = adapterStu
        students.clear()
        students.addAll(stuInfoDao.getAllStudents(databaseHelper))
        adapterStu.notifyDataSetChanged()
        customToast(msg, R.layout.toast_view)
    }

    private fun showDialogSearch(adapterStu: StudentAdapter) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.search_layout, null)
        builder.setView(dialogView)

        val name: EditText = dialogView.findViewById(R.id.searchName) // 文本框

        // 专业选项的下拉列表
        val major: Spinner = dialogView.findViewById(R.id.majorSpinnerSearch)
        // 学院选项的下拉列表
        val institution: Spinner = dialogView.findViewById(R.id.institutionSpinnerSearch)
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

        val buttonSubmit: Button = dialogView.findViewById(R.id.searchBtn)

        val dialog = builder.create()
        dialog.show()

        buttonSubmit.setOnClickListener {

            val inputName = name.text.toString()
            val selectedInstitution = institution.selectedItem.toString()
            val selectedMajor = major.selectedItem.toString()

            val filteredStudents =
                stuInfoDao.queryStudents(
                    databaseHelper,
                    selectedInstitution,
                    selectedMajor,
                    inputName
                )
            if (filteredStudents.isNotEmpty()) { // 查到结果，改变适配器
                filteredStu = StudentAdapter(this, R.layout.inner_list_layout, filteredStudents)
                listViewStudents.adapter = filteredStu
                filteredStu.notifyDataSetChanged()
                customToast("查询成功，共有${filteredStudents.size}个结果", R.layout.toast_view)
                dialog.dismiss() // 退出弹窗
            } else { // 没查到结果，提示
                customToast("无查询结果", R.layout.toast_view_e)
            }
        }
    }

    private fun showDialog(adapterStu: StudentAdapter, item: String? = "") {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.activity_main, null)
        builder.setView(dialogView)

        val name: EditText = dialogView.findViewById(R.id.et_name)
        val stuId: EditText = dialogView.findViewById(R.id.et_student_id)
        if (item != "") stuId.setText(item)

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

        val intro = dialogView.findViewById<EditText>(R.id.et_stu_Intro)
        val motto = dialogView.findViewById<EditText>(R.id.et_motto)

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
            Log.i("新增数据", "showDialog ${stuId.text}")
            try{
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
                    curDay,
                    intro.text.toString(),
                    motto.text.toString()
                )
                Log.i("新增数据", "showDialog $data")
                if (data.stuName.isNotEmpty() && data.stuId.isNotEmpty() && data.sex != "null") {
                    // 将新学生对象添加到列表
                    if (stuInfoDao.insertStudent(databaseHelper, data)) {
                        stuExtInfoDao.insertStudentExt(databaseHelper, data)
                        // 刷新数据
                        students.clear()
                        students.addAll(stuInfoDao.getAllStudents(databaseHelper))
                        customToast("数据已提交", R.layout.toast_view)
                    } else {
                        customToast("学号冲突", R.layout.toast_view_e)
                    }
                    // 通知适配器数据已改变
                    adapterStu.notifyDataSetChanged()
                    dialog.dismiss()
                } else {
                    customToast("修改数据失败，至少输入姓名、性别和学号！", R.layout.toast_view_e)
                }
            }catch (e: Exception){
                Log.e("提交错误", "showDialog ${e.message}")
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
                    val stuId = itemStu.stuId
                    stuInfoDao.deleteStudent(databaseHelper, stuId)
                    stuExtInfoDao.deleteStudentExt(databaseHelper, stuId)
                    students.clear()
                    students.addAll(stuInfoDao.getAllStudents(databaseHelper))

                    Log.i("删除操作", "删除成功$students")

                    // 通知适配器数据已改变
                    adapterStu.notifyDataSetChanged()

                    alertDialog.dismiss() // 关闭操作选择对话框
                    reset("数据已删除") // 刷新回到主界面
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
        institution.setSelection(institutions.indexOf(itemStu.institution)) // 设置初始学院

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

                // 设置专业下拉列表默认值
                if (selectedItem == institutions[0]) { // 计算机学院
                    val majors = resources.getStringArray(R.array.cs)
                    major.setSelection(majors.indexOf(itemStu.major))
                } else if (selectedItem == institutions[1]) { // 电气学院
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

        val intro = dialogView.findViewById<EditText>(R.id.et_stu_Intro)
        intro.setText(itemStu.intro)
        val motto = dialogView.findViewById<EditText>(R.id.et_motto)
        motto.setText(itemStu.motto)

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
                curDay,
                intro.text.toString(),
                motto.text.toString()
            )
            Log.i("学生新增", "data: $data")
            if (data.stuName.isNotEmpty() && data.stuId.isNotEmpty() && data.sex != "null") {
                // 执行更新
                if (stuInfoDao.updateStudent(databaseHelper, data) &&
                    stuExtInfoDao.updateStudentExt(databaseHelper, data)
                ) {
                    students.clear()
                    students.addAll(stuInfoDao.getAllStudents(databaseHelper))
                    Log.i("修改操作", "修改成功")
                    customToast("数据已提交", R.layout.toast_view)
                } else {
                    Log.e("修改操作", "修改失败")
                    customToast("修改数据失败", R.layout.toast_view_e)
                }

                // 通知适配器数据已改变
                adapterStu.notifyDataSetChanged()

                dialog.dismiss()
                reset("数据修改成功") // 刷新回到主界面
            } else {
                customToast("修改数据失败，至少输入姓名、性别和学号！", R.layout.toast_view_e)
            }
        }
        dialog.show()
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

