package com.losgai.works.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.losgai.works.R
import com.losgai.works.entity.Student

class StudentAdapter(context: Context, resource: Int, objects: MutableList<Student>) :
    ArrayAdapter<Student>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val student = getItem(position) ?: return convertView ?: View(context)
        val view = LayoutInflater.from(context).inflate(R.layout.inner_list_layout, parent, false)

        val stuImage = view.findViewById<ImageView>(R.id.imageUrl)
        val stuId = view.findViewById<TextView>(R.id.stuId)
        val stuName = view.findViewById<TextView>(R.id.stuName)
        val stuSex = view.findViewById<TextView>(R.id.stuSex)
        val stuInstitution = view.findViewById<TextView>(R.id.insitution)
        val stuMajor = view.findViewById<TextView>(R.id.major)
        val stuHobby = view.findViewById<TextView>(R.id.hobby)

        // Set the image and text for the views
        stuImage.setImageResource(student.imageUrl)
        stuId.text = "学号： " + student.stuId
        stuName.text = "姓名： " + student.stuName
        stuSex.text = "性别： " + student.sex
        stuInstitution.text = "学院： " + student.institution
        stuMajor.text = "专业： " + student.major
        stuHobby.text = "爱好： " + student.hobby

        return view
    }
}

