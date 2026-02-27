package com.mono9rome.typst_note_app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mono9rome.typst_note_app.di.AppComponent
import com.mono9rome.typst_note_app.di.create

fun main() = application {

    val appComponent = AppComponent::class.create()

    Window(
        onCloseRequest = ::exitApplication,
        title = "TypstNoteApp",
    ) {
        App(appComponent = appComponent)
    }
}