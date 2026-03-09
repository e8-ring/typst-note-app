package com.mono9rome.typst_note_app.di

import com.mono9rome.typst_note_app.data.LocalFileManager
import com.mono9rome.typst_note_app.core.renderer.MathRenderer
import com.mono9rome.typst_note_app.core.renderer.RustMathRenderer
import com.mono9rome.typst_note_app.ui.AppScreenViewModel
import com.mono9rome.typst_note_app.ui.sidebar.NoteChooserViewModel
import com.mono9rome.typst_note_app.ui.viewer.renderer.NestedContentRendererViewModel
import me.tatarka.inject.annotations.Provides

abstract class AppComponent {

    protected val RustMathRenderer.bind: MathRenderer
        @Provides get() = this

    abstract  val fileManager: LocalFileManager
    abstract val mathRenderer: MathRenderer
    abstract val appScreenViewModelProvider: () -> AppScreenViewModel
    abstract val noteChooserViewModelProvider: () -> NoteChooserViewModel
    abstract val nestedContentRendererViewModelProvider: () -> NestedContentRendererViewModel
}