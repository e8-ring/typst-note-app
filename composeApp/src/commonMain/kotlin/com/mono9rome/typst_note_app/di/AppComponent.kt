package com.mono9rome.typst_note_app.di

import com.mono9rome.typst_note_app.core.renderer.MathRenderer
import com.mono9rome.typst_note_app.core.renderer.RustMathRenderer
import com.mono9rome.typst_note_app.data.LocalFileManager
import com.mono9rome.typst_note_app.ui.AppScreenViewModel
import com.mono9rome.typst_note_app.ui.editor.EditorTabsViewModel
import com.mono9rome.typst_note_app.ui.editor.MetadataEditorViewModel
import com.mono9rome.typst_note_app.ui.editor.SourceEditorViewModel
import com.mono9rome.typst_note_app.ui.sidebar.SidebarViewModel
import com.mono9rome.typst_note_app.ui.sidebar.content.NoteChooserViewModel
import com.mono9rome.typst_note_app.ui.sidebar.content.NoteTagsViewModel
import com.mono9rome.typst_note_app.ui.viewer.ContentViewerViewModel
import com.mono9rome.typst_note_app.ui.viewer.renderer.NestedContentRendererViewModel
import me.tatarka.inject.annotations.Provides

abstract class AppComponent {

    protected val RustMathRenderer.bind: MathRenderer
        @Provides get() = this

    abstract  val fileManager: LocalFileManager
    abstract val mathRenderer: MathRenderer
    abstract val appScreenViewModelProvider: () -> AppScreenViewModel
    /* --- サイドバー関連 ViewModel --- */
    abstract val sidebarViewModelProvider: () -> SidebarViewModel
    abstract val noteChooserViewModelProvider: () -> NoteChooserViewModel
    abstract val noteTagsViewModelProvider: () -> NoteTagsViewModel
    /* --- エディタ関連 ViewModel --- */
    abstract val editorTabsViewModelProvider: () -> EditorTabsViewModel
    abstract val metadataEditorViewModelProvider: () -> MetadataEditorViewModel
    abstract val sourceEditorViewModelProvider: () -> SourceEditorViewModel
    abstract val contentViewerViewModelProvider: () -> ContentViewerViewModel
    abstract val nestedContentRendererViewModelProvider: () -> NestedContentRendererViewModel
}