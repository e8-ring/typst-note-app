package com.mono9rome.typst_note_app.di

import com.mono9rome.typst_note_app.NoteFieldViewModel
import com.mono9rome.typst_note_app.render.MathRenderer
import com.mono9rome.typst_note_app.render.RustMathRenderer
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
abstract class AppComponent {

    protected val RustMathRenderer.bind: MathRenderer
        @Provides get() = this

    abstract val mathRenderer: MathRenderer
    abstract val noteFieldViewModelProvider: () -> NoteFieldViewModel
}