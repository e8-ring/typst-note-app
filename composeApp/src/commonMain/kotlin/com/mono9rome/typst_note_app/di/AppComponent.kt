package com.mono9rome.typst_note_app.di

import com.mono9rome.typst_note_app.data.LocalFileManager
import com.mono9rome.typst_note_app.ui.NoteFieldViewModel
import com.mono9rome.typst_note_app.render.MathRenderer
import com.mono9rome.typst_note_app.render.RustMathRenderer
import com.mono9rome.typst_note_app.ui.MainScreenViewModel
import com.mono9rome.typst_note_app.ui.sidebar.NoteChooserViewModel
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

abstract class AppComponent {

    protected val RustMathRenderer.bind: MathRenderer
        @Provides get() = this

    abstract  val fileManager: LocalFileManager
    abstract val mathRenderer: MathRenderer
    abstract val mainScreenViewModelProvider: () -> MainScreenViewModel
    abstract val noteFieldViewModelProvider: () -> NoteFieldViewModel
    abstract val noteChooserViewModelProvider: () -> NoteChooserViewModel
}