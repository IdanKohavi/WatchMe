package com.example.watchme.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.watchme.R
import com.example.watchme.utils.AppLanguageManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    @Inject
    lateinit var languageManager: AppLanguageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageManager.updateLanguage() // set language
        setContentView(R.layout.main_activity)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        languageManager.updateLanguage()
    }
}


