package com.mono9rome.typst_note_app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {

    val appComponent = JvmAppComponent::class.create()

    Window(
        onCloseRequest = ::exitApplication,
        title = "TypstNoteApp",
    ) {
        App(appComponent = appComponent)
    }
}