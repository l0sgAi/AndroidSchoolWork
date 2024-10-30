package com.losgai.works.helper

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.losgai.works.R
import com.losgai.works.entity.Student
import java.security.GeneralSecurityException
import java.security.MessageDigest

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val defaultAvatar = R.drawable.user

    companion object {
        private const val DATABASE_NAME = "user.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "users"
        private const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"

        const val TABLE_STUDENT = "student"
        const val COLUMN_IMAGE_URL = "image_url"
        const val COLUMN_STU_ID = "stu_id"
        const val COLUMN_STU_NAME = "stu_name"
        const val COLUMN_SEX = "sex"
        const val COLUMN_INSTITUTION = "institution"
        const val COLUMN_MAJOR = "major"
        const val COLUMN_HOBBY = "hobby"
        const val COLUMN_BIRTH_YEAR = "birth_year"
        const val COLUMN_BIRTH_MONTH = "birth_month"
        const val COLUMN_BIRTH_DAY = "birth_day"
    }

    override fun onCreate(db: SQLiteDatabase) { // 初始化数据表
        val createTableUser = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT not null, " +
                "$COLUMN_PASSWORD TEXT not null)"
        db.execSQL(createTableUser)
        Log.i("初始化数据库", "用户数据库创建成功")

        val createStudentTableQuery = "CREATE TABLE $TABLE_STUDENT (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_IMAGE_URL INTEGER, " +
                "$COLUMN_STU_ID TEXT not null, " +
                "$COLUMN_STU_NAME TEXT not null, " +
                "$COLUMN_SEX TEXT not null, " +
                "$COLUMN_INSTITUTION TEXT, " +
                "$COLUMN_MAJOR TEXT, " +
                "$COLUMN_HOBBY TEXT, " +
                "$COLUMN_BIRTH_YEAR INTEGER, " +
                "$COLUMN_BIRTH_MONTH INTEGER, " +
                "$COLUMN_BIRTH_DAY INTEGER )"
        db.execSQL(createStudentTableQuery)
        Log.i("初始化数据库", "学生数据表创建成功")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 升级数据库时执行的操作
    }

    fun insertInitialUserIfEmpty(
        username: String,
        password: String,
        context: Context
    ) { // 如果用户数据库为空就插入一条
        val db = this.readableDatabase
        val countQuery = "SELECT COUNT(*) FROM $TABLE_NAME"
        val cursor = db.rawQuery(countQuery, null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if (count == 0) {
            val encryptedPassword = encryptPassword(password)
            Log.i("初始化数据库：", encryptedPassword)
            val insertQuery =
                "INSERT INTO $TABLE_NAME ($COLUMN_USERNAME, $COLUMN_PASSWORD) VALUES (?,?)"
            val insertStmt = db.compileStatement(insertQuery)
            insertStmt.bindString(1, username)
            insertStmt.bindString(2, encryptedPassword)
            insertStmt.executeInsert()
            Log.i("初始化数据库", "初始用户已添加")
        }
        db.close()
    }

    fun insertInitialStudentIfEmpty(context: Context) { // 如果学生数据库为空就插入初始数据
        val db = this.readableDatabase
        val countQuery = "SELECT COUNT(*) FROM $TABLE_STUDENT"
        val cursor = db.rawQuery(countQuery, null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if (count == 0) { // 插入初始学生数据
            val insertQuery =
                "INSERT INTO $TABLE_STUDENT (" +
                        "$COLUMN_IMAGE_URL," +
                        "$COLUMN_STU_ID," +
                        "$COLUMN_STU_NAME," +
                        "$COLUMN_SEX," +
                        "$COLUMN_INSTITUTION," +
                        "$COLUMN_MAJOR," +
                        "$COLUMN_HOBBY," +
                        "$COLUMN_BIRTH_YEAR," +
                        "$COLUMN_BIRTH_MONTH," +
                        "$COLUMN_BIRTH_DAY) VALUES " +
                        "($defaultAvatar,\"3305\",\"张三\",\"男\",\"计算机与通信工程学院\",\"计算机科学与技术\",\"音乐\",2001,0,1)," +
                        "($defaultAvatar,\"3306\",\"李四\",\"女\",\"计算机与通信工程学院\",\"软件工程\",\"跑步\",2002,1,2)," +
                        "($defaultAvatar,\"3307\",\"王五\",\"女\",\"电气学院\",\"电机工程\",\"游泳\",2003,3,4)"
            val insertStmt = db.compileStatement(insertQuery)
            insertStmt.executeInsert()
            Log.i("初始化数据库", "初始学生信息已添加")
            db.close()
        }
    }

    fun insertStudent(student: Student): Boolean { // 插入学生信息
        val imageUrl = student.imageUrl
        val stuId = student.stuId
        val stuName = student.stuName
        val sex = student.sex
        val institution = student.institution
        val major = student.major
        val hobby = student.hobby
        val birthYear = student.birthYear
        val birthMonth = student.birthMonth
        val birthday = student.birhday

        // 保证学生id不重复
        val dbRead = this.readableDatabase
        val countQuery = "SELECT COUNT(*) FROM $TABLE_STUDENT WHERE $COLUMN_STU_ID = $stuId"
        val cursor = dbRead.rawQuery(countQuery, null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if (count == 0) { // 没有初始数据的情况
            try {
                val dbWrite = this.writableDatabase
                val insertQuery =
                    "INSERT INTO $TABLE_STUDENT (" +
                            "$COLUMN_IMAGE_URL," +
                            "$COLUMN_STU_ID," +
                            "$COLUMN_STU_NAME," +
                            "$COLUMN_SEX," +
                            "$COLUMN_INSTITUTION," +
                            "$COLUMN_MAJOR," +
                            "$COLUMN_HOBBY," +
                            "$COLUMN_BIRTH_YEAR," +
                            "$COLUMN_BIRTH_MONTH," +
                            "$COLUMN_BIRTH_DAY) VALUES " +
                            "($imageUrl,'$stuId','$stuName','$sex','$institution','$major','$hobby',$birthYear,$birthMonth,$birthday)"
                val insertStmt = dbWrite.compileStatement(insertQuery)
                insertStmt.executeInsert()
                dbWrite.close()
                return true
            } catch (e: Exception) {
                Log.e("ERROR", "插入学生数据失败: ${e.message}")
            }
        }

        return false
    }

    fun deleteStudent(stuId: String): Boolean { // 删除学生信息
        val db = this.writableDatabase
        val deleteSql =
            "DELETE FROM $TABLE_STUDENT WHERE $COLUMN_STU_ID ='$stuId'"
        val deleteStmt = db.compileStatement(deleteSql)
        try {
            deleteStmt.executeUpdateDelete()
            Log.i("操作数据库", "学号为 $stuId 的学生信息已删除")
            db.close()
            return true
        } catch (e: Exception) {
            Log.e("ERROR", "删除学生数据失败: ${e.message}")
        }
        return false
    }

    fun updateStudent(student: Student): Boolean { // 更新学生信息
        val imageUrl = student.imageUrl
        val stuId = student.stuId
        val stuName = student.stuName
        val sex = student.sex
        val institution = student.institution
        val major = student.major
        val hobby = student.hobby
        val birthYear = student.birthYear
        val birthMonth = student.birthMonth
        val birthday = student.birhday

        if (stuId.isNotEmpty() && stuName.isNotEmpty() && sex.isNotEmpty()) {
            try {
                val dbWrite = this.writableDatabase
                val updateQuery =
                    "UPDATE $TABLE_STUDENT SET " +
                            "$COLUMN_IMAGE_URL=$imageUrl," +
                            "$COLUMN_STU_NAME='$stuName'," +
                            "$COLUMN_SEX='$sex'," +
                            "$COLUMN_INSTITUTION='$institution'," +
                            "$COLUMN_MAJOR='$major'," +
                            "$COLUMN_HOBBY='$hobby'," +
                            "$COLUMN_BIRTH_YEAR=$birthYear," +
                            "$COLUMN_BIRTH_MONTH=$birthMonth," +
                            "$COLUMN_BIRTH_DAY=$birthday" +
                            " WHERE $COLUMN_STU_ID='$stuId'"
                val updateStmt = dbWrite.compileStatement(updateQuery)
                updateStmt.executeUpdateDelete()
                dbWrite.close()
                return true
            } catch (e: Exception) {
                Log.e("ERROR", "插入学生数据失败: ${e.message}")
            }
        }
        return false
    }

    @SuppressLint("Range")
    fun getAllStudents(): MutableList<Student> { // 查询并返回数据库中所有的学生信息
        val db = this.readableDatabase
        val studentList = ArrayList<Student>()

        val cursor: Cursor? = db.query(
            TABLE_STUDENT,
            null,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val imageUrl = it.getInt(it.getColumnIndex(COLUMN_IMAGE_URL))
                    val stuId = it.getString(it.getColumnIndex(COLUMN_STU_ID))
                    val stuName = it.getString(it.getColumnIndex(COLUMN_STU_NAME))
                    val sex = it.getString(it.getColumnIndex(COLUMN_SEX))
                    val institution = it.getString(it.getColumnIndex(COLUMN_INSTITUTION))
                    val major = it.getString(it.getColumnIndex(COLUMN_MAJOR))
                    val hobby = it.getString(it.getColumnIndex(COLUMN_HOBBY))
                    val birthYear = it.getInt(it.getColumnIndex(COLUMN_BIRTH_YEAR))
                    val birthMonth = it.getInt(it.getColumnIndex(COLUMN_BIRTH_MONTH))
                    val birthDay = it.getInt(it.getColumnIndex(COLUMN_BIRTH_DAY))
                    val student = Student(
                        imageUrl,
                        stuId,
                        stuName,
                        sex,
                        institution,
                        major,
                        hobby,
                        birthYear,
                        birthMonth,
                        birthDay
                    )
                    studentList.add(student)
                } while (it.moveToNext())
            }
        }
        db.close()
        return studentList
    }

    @SuppressLint("Range")
    fun queryStudents(
        institutionInput: String,
        majorInput: String,
        nameInput: String
    ): MutableList<Student> { // 查询并返回数据库中所有的学生信息
        // SELECT * FROM student WHERE institution = '计算机与通信工程学院' and major='计算机科学与技术' and stu_name like '%云%'
        val db = this.readableDatabase
        val studentList = ArrayList<Student>()

        val selection = "$COLUMN_INSTITUTION = ? AND $COLUMN_MAJOR = ? AND $COLUMN_STU_NAME LIKE ?"
        val selectionArgs = arrayOf(
            institutionInput,
            majorInput,
            "%${nameInput}%"
        )

        val cursor: Cursor? = db.query(
            TABLE_STUDENT,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val imageUrl = it.getInt(it.getColumnIndex(COLUMN_IMAGE_URL))
                    val stuId = it.getString(it.getColumnIndex(COLUMN_STU_ID))
                    val stuName = it.getString(it.getColumnIndex(COLUMN_STU_NAME))
                    val sex = it.getString(it.getColumnIndex(COLUMN_SEX))
                    val institution = it.getString(it.getColumnIndex(COLUMN_INSTITUTION))
                    val major = it.getString(it.getColumnIndex(COLUMN_MAJOR))
                    val hobby = it.getString(it.getColumnIndex(COLUMN_HOBBY))
                    val birthYear = it.getInt(it.getColumnIndex(COLUMN_BIRTH_YEAR))
                    val birthMonth = it.getInt(it.getColumnIndex(COLUMN_BIRTH_MONTH))
                    val birthDay = it.getInt(it.getColumnIndex(COLUMN_BIRTH_DAY))
                    val student = Student(
                        imageUrl,
                        stuId,
                        stuName,
                        sex,
                        institution,
                        major,
                        hobby,
                        birthYear,
                        birthMonth,
                        birthDay
                    )
                    studentList.add(student)
                } while (it.moveToNext())
            }
        }

        db.close()
        return studentList
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
                if (hexStr.length == 1) {
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
