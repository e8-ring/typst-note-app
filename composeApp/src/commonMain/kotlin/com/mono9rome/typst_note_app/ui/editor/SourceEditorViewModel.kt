package com.mono9rome.typst_note_app.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mono9rome.typst_note_app.FontSizeProvider
import com.mono9rome.typst_note_app.core.state.EditorStateManager
import com.mono9rome.typst_note_app.core.state.ViewerStateManager
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.model.SourceCode
import com.mono9rome.typst_note_app.util.mapState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class SourceEditorViewModel(
    private val fontSizeProvider: FontSizeProvider,
    private val editorStateManager: EditorStateManager,
    private val viewerStateManager: ViewerStateManager,
) : ViewModel() {

    data class UiState(
        val currentNoteId: Note.Id,
        val currentNoteSourceCode: SourceCode,
        val fontSizeSp: Float,
    )

    val uiState: StateFlow<UiState?> =
        editorStateManager.editorState.mapState(viewModelScope) { editorState ->
            editorState.focusedNote?.let { note ->
                UiState(
                    currentNoteId = note.id,
                    currentNoteSourceCode = note.sourceCode,
                    fontSizeSp = fontSizeProvider.current
                )
            }
        }

    fun updateSourceCode(sourceCode: SourceCode) {
        viewModelScope.launch {
            editorStateManager.updateFocusedNoteSourceCode(sourceCode)
            viewerStateManager.render()
        }
    }
}