package com.example.watchme.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.example.watchme.R

fun showSuccessToast(context: Context, message: String) {
    val layout = LayoutInflater.from(context).inflate(R.layout.success_toast, null)
    val textView = layout.findViewById<TextView>(R.id.toast_message)
    textView.text = message

    val toast = Toast(context)
    toast.duration = Toast.LENGTH_SHORT
    toast.view = layout
    toast.show()
}