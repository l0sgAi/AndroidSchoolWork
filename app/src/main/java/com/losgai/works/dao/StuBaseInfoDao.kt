package com.losgai.works.dao

import android.annotation.SuppressLint
import android.database.Cursor
import android.util.Log
import com.losgai.works.entity.Student
import com.losgai.works.helper.DatabaseHelper
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_BIRTH_DAY
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_BIRTH_MONTH
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_BIRTH_YEAR
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_HOBBY
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_IMAGE_URL
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_INSTITUTION
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_INTRO
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_MAJOR
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_MOTTO
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_SEX
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_STU_ID
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_STU_NAME
import com.losgai.works.helper.DatabaseHelper.Companion.TABLE_STUDENT
import com.losgai.works.helper.DatabaseHelper.Companion.TABLE_STUDENT_EXT

class StuBaseInfoDao { // 学生表的基础操作

    fun insertStudent(helper: DatabaseHelper, student: Student): Boolean { // 插入学生信息
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
        val dbRead = helper.readableDatabase
        val countQuery = "SELECT COUNT(*) FROM $TABLE_STUDENT WHERE $COLUMN_STU_ID = '$stuId'"
        val cursor = dbRead.rawQuery(countQuery, null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if (count == 0) { // 没有初始数据的情况
            try {
                val dbWrite = helper.writableDatabase
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

    fun deleteStudent(helper: DatabaseHelper, stuId: String): Boolean { // 删除学生信息
        val db = helper.writableDatabase
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

    fun updateStudent(helper: DatabaseHelper, student: Student): Boolean { // 更新学生信息
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
                val dbWrite = helper.writableDatabase
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
                            "$COLUMN_BIRTH_DAY=$birthday " +
                            "WHERE $COLUMN_STU_ID='$stuId'"
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
    fun getAllStudents(helper: DatabaseHelper): MutableList<Student> { // 查询并返回数据库中所有的学生信息
        val db = helper.readableDatabase
        val studentList = ArrayList<Student>()
        val sqlQueryAll = "SELECT " +
                "$COLUMN_IMAGE_URL," +
                "$TABLE_STUDENT_EXT.$COLUMN_STU_ID," +
                "$COLUMN_STU_NAME," +
                "$COLUMN_SEX," +
                "$COLUMN_INSTITUTION," +
                "$COLUMN_MAJOR," +
                "$COLUMN_HOBBY," +
                "$COLUMN_BIRTH_YEAR," +
                "$COLUMN_BIRTH_MONTH," +
                "$COLUMN_BIRTH_DAY," +
                "$COLUMN_INTRO," +
                "$COLUMN_MOTTO " +
                "FROM $TABLE_STUDENT " +
                "INNER JOIN $TABLE_STUDENT_EXT " +
                "ON $TABLE_STUDENT.$COLUMN_STU_ID = $TABLE_STUDENT_EXT.$COLUMN_STU_ID"

        val cursor: Cursor? = db.rawQuery(sqlQueryAll,null)

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
                    val intro = it.getString(it.getColumnIndex(COLUMN_INTRO))
                    val motto = it.getString(it.getColumnIndex(COLUMN_MOTTO))
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
                        birthDay,
                        intro,
                        motto
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
        helper: DatabaseHelper,
        institutionInput: String,
        majorInput: String,
        nameInput: String
    ): MutableList<Student> { // 查询并返回数据库中所有的学生信息
        // SELECT * FROM student WHERE institution = '计算机与通信工程学院' and major='计算机科学与技术' and stu_name like '%云%'
        val db = helper.readableDatabase
        val studentList = ArrayList<Student>()
        val selectionArgs = arrayOf(institutionInput, majorInput, "%$nameInput%")

        val sqlQueryAll = "SELECT " +
                "$COLUMN_IMAGE_URL," +
                "$TABLE_STUDENT_EXT.$COLUMN_STU_ID," +
                "$COLUMN_STU_NAME," +
                "$COLUMN_SEX," +
                "$COLUMN_INSTITUTION," +
                "$COLUMN_MAJOR," +
                "$COLUMN_HOBBY," +
                "$COLUMN_BIRTH_YEAR," +
                "$COLUMN_BIRTH_MONTH," +
                "$COLUMN_BIRTH_DAY," +
                "$COLUMN_INTRO," +
                "$COLUMN_MOTTO " +
                "FROM $TABLE_STUDENT " +
                "INNER JOIN $TABLE_STUDENT_EXT " +
                "ON $TABLE_STUDENT.$COLUMN_STU_ID = $TABLE_STUDENT_EXT.$COLUMN_STU_ID " +
                "WHERE $COLUMN_INSTITUTION = ? AND $COLUMN_MAJOR = ? AND $COLUMN_STU_NAME like ?"

        val cursor: Cursor? = db.rawQuery(sqlQueryAll,selectionArgs)

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
                    val intro = it.getString(it.getColumnIndex(COLUMN_INTRO))
                    val motto = it.getString(it.getColumnIndex(COLUMN_MOTTO))
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
                        birthDay,
                        intro,
                        motto
                    )
                    studentList.add(student)
                } while (it.moveToNext())
            }
        }

        db.close()
        return studentList
    }
}