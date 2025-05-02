package com.example.watchme.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
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

fun getImagePathFromUri(context: Context, uri: Uri): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
            if (columnIndex >= 0) {
                return it.getString(columnIndex)
            }
        }
    }
    return uri.toString()
}