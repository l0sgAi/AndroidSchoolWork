package com.losgai.works.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.losgai.works.R
import com.losgai.works.entity.Student
import java.security.GeneralSecurityException
import java.security.MessageDigest

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "user.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "users_info"
        private const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"

        const val TABLE_STUDENT = "student_base_info"
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

        const val TABLE_STUDENT_EXT = "student_ext_info"
        const val COLUMN_INTRO = "stu_intro"
        const val COLUMN_MOTTO = "stu_motto"
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

        val createStudentExtTableQuery = "CREATE TABLE $TABLE_STUDENT_EXT (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_STU_ID TEXT not null, " +
                "$COLUMN_INTRO TEXT , " +
                "$COLUMN_MOTTO TEXT)"
        db.execSQL(createStudentExtTableQuery)
        Log.i("初始化数据库", "学生扩展数据表创建成功")
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

        if (count == 0) {
            val insertQuery = // 插入学生数据
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
                        "(${R.drawable.user},\"3305\",\"张三\",\"男\",\"计算机与通信工程学院\",\"计算机科学与技术\",\"音乐\",2001,0,1)," +
                        "(${R.drawable.user},\"3306\",\"李四\",\"女\",\"计算机与通信工程学院\",\"软件工程\",\"跑步\",2002,1,2)," +
                        "(${R.drawable.user},\"3307\",\"王五\",\"女\",\"电气学院\",\"电机工程\",\"游泳\",2003,3,4)"
            val insertStmt = db.compileStatement(insertQuery)
            insertStmt.executeInsert()
            Log.i("初始化数据库", "学生信息已添加")

            val insertQueryExt = // 插入额外学生数据
                "INSERT INTO $TABLE_STUDENT_EXT (" +
                        "$COLUMN_STU_ID," +
                        "$COLUMN_INTRO," +
                        "$COLUMN_MOTTO) VALUES " +
                        "(\"3305\",\"张三个人简介\",\"张三左右铭\")," +
                        "(\"3306\",\"李四个人简介\",\"李四左右铭\")," +
                        "(\"3307\",\"王五个人简介\",\"王五左右铭\")"
            val insertStmtExt = db.compileStatement(insertQueryExt)
            insertStmtExt.executeInsert()
            Log.i("初始化数据库", "扩展学生信息已添加")
            db.close()
        }
    }

    fun loadStudent(students: MutableList<Student>): Boolean { // 加载之前保存的学生信息
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            if (students.size > 0) {
                val deleteSql =
                    "DELETE FROM $TABLE_STUDENT"
                val deleteStmt = db.compileStatement(deleteSql)
                deleteStmt.executeUpdateDelete()

                val deleteSqlExt =
                    "DELETE FROM $TABLE_STUDENT_EXT"
                val deleteStmtExt = db.compileStatement(deleteSqlExt)
                deleteStmtExt.executeUpdateDelete()
                Log.i("操作数据库", "学生信息已全部删除")

                var insertQuery = // 尝试插入解析的信息
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
                            "$COLUMN_BIRTH_DAY) VALUES "

                for (student in students) {
                    insertQuery += "(${R.drawable.user},'${student.stuId}'," +
                            "'${student.stuName}','${student.sex}'," +
                            "'${student.institution}','${student.major}'," +
                            "'${student.hobby}',${student.birthYear}," +
                            "${student.birthMonth},${student.birhday}),"
                }
                insertQuery = insertQuery.dropLast(1)
                val insertStmt = db.compileStatement(insertQuery)
                insertStmt.executeInsert()

                var insertQueryExt = // 尝试插入解析的额外信息
                    "INSERT INTO $TABLE_STUDENT_EXT (" +
                            "$COLUMN_STU_ID," +
                            "$COLUMN_INTRO," +
                            "$COLUMN_MOTTO) VALUES "
                for (student in students) {
                    insertQueryExt += "(${student.stuId},'${student.intro}'," +
                            "'${student.motto}'),"
                }
                insertQueryExt = insertQueryExt.dropLast(1)
                val insertStmtExt = db.compileStatement(insertQueryExt)
                insertStmtExt.executeInsert()


                Log.i("导入操作执行完成", insertQuery)
            }
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
            return true
        } catch (e: Exception) {
            db.endTransaction()
            db.close()
            Log.e("ERROR", "删除学生数据失败: ${e.message}")
        }

        return false
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
            Log.e("加密密码", "加密失败: ${e.message}")
            return password
        }
    }
}
