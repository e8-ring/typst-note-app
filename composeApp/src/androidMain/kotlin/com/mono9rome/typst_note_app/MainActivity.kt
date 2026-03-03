package com.mono9rome.typst_note_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mono9rome.typst_note_app.di.create

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appComponent = AndroidAppComponent::class.create(applicationContext)

        setContent {
            App(appComponent)
        }
    }
}