package com.losgai.works.dao

import android.util.Log
import com.losgai.works.entity.Student
import com.losgai.works.helper.DatabaseHelper
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_INTRO
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_MOTTO
import com.losgai.works.helper.DatabaseHelper.Companion.COLUMN_STU_ID
import com.losgai.works.helper.DatabaseHelper.Companion.TABLE_STUDENT_EXT

class StuExtInfoDao { // 学生表的扩展操作

    fun insertStudentExt(helper: DatabaseHelper, student: Student): Boolean { // 插入学生信息
        val stuId = student.stuId
        val intro = student.intro
        val motto = student.motto

        // 保证学生信息不重复,删除现有信息
        val dbWrite = helper.writableDatabase
        val deleteSqlExt =
            "DELETE FROM $TABLE_STUDENT_EXT WHERE $COLUMN_STU_ID ='$stuId'"
        val deleteStmtExt = dbWrite.compileStatement(deleteSqlExt)
        deleteStmtExt.executeUpdateDelete()

        try {

            val insertQuery =
                "INSERT INTO $TABLE_STUDENT_EXT (" +
                        "$COLUMN_STU_ID," +
                        "$COLUMN_INTRO," +
                        "$COLUMN_MOTTO) VALUES " +
                        "($stuId,'$intro','$motto')"
            val insertStmt = dbWrite.compileStatement(insertQuery)
            insertStmt.executeInsert()
            dbWrite.close()
            return true
        } catch (e: Exception) {
            Log.e("ERROR", "插入额外学生数据失败: ${e.message}")
        }

        return false
    }

    fun deleteStudentExt(helper: DatabaseHelper, stuId: String): Boolean { // 删除学生信息
        val db = helper.writableDatabase
        val deleteSql =
            "DELETE FROM $TABLE_STUDENT_EXT WHERE $COLUMN_STU_ID ='$stuId'"
        val deleteStmt = db.compileStatement(deleteSql)
        try {
            deleteStmt.executeUpdateDelete()
            Log.i("操作数据库", "学号为 $stuId 的学生额外信息已删除")
            db.close()
            return true
        } catch (e: Exception) {
            Log.e("ERROR", "删除额外学生数据失败: ${e.message}")
        }
        return false
    }

    fun updateStudentExt(helper: DatabaseHelper, student: Student): Boolean { // 更新额外学生信息
        val stuId = student.stuId
        val intro = student.intro
        val motto = student.motto

        if (stuId.isNotEmpty()) {
            try {
                val dbWrite = helper.writableDatabase
                val updateQuery =
                    "UPDATE $TABLE_STUDENT_EXT SET " +
                            "$COLUMN_INTRO='$intro'," +
                            "$COLUMN_MOTTO='$motto' " +
                            "WHERE $COLUMN_STU_ID='$stuId'"
                val updateStmt = dbWrite.compileStatement(updateQuery)
                updateStmt.executeUpdateDelete()
                dbWrite.close()
                return true
            } catch (e: Exception) {
                Log.e("ERROR", "插入学生额外数据失败: ${e.message}")
            }
        }
        return false
    }

}