package com.mono9rome.typst_note_app.ui.state

import com.mono9rome.typst_note_app.model.ContentBlock
import kotlinx.coroutines.flow.MutableStateFlow

typealias MainMutableStateFlow = MutableStateFlow<MainUiState>

data class MainUiState(
    val editorState: EditorState,
    val fontSizeSp: Float,
    val currentRenderedContent: List<ContentBlock>,
    val isCompileError: Boolean,
) {
    companion object {
        val default = MainUiState(
            editorState = EditorState.default,
            fontSizeSp = 14f,
            currentRenderedContent = listOf(),
            isCompileError = false
        )
    }
}