package com.example.watchme.utils

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLanguageManager @Inject constructor() {

    private val _language = MutableLiveData(getSystemLanguage())
    val language: LiveData<String> get() = _language

    fun updateLanguage() {
        val newLang = getSystemLanguage()
        if (_language.value != newLang) {
            _language.value = newLang
        }
    }

    fun getLanguage() : String{
        return _language.value ?: getSystemLanguage()
    }

    private fun getSystemLanguage(): String {
        return if (Locale.getDefault().language == "he") "he-IL" else "en-US"
    }

}