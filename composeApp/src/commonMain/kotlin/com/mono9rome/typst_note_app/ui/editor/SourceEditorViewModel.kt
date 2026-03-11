package com.mono9rome.typst_note_app.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mono9rome.typst_note_app.FontSizeProvider
import com.mono9rome.typst_note_app.core.state.EditorStateManager
import com.mono9rome.typst_note_app.core.state.ViewerStateManager
import com.mono9rome.typst_note_app.model.Note
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
        val currentNoteSource: Note.Source,
        val fontSizeSp: Float,
    )

    val uiState: StateFlow<UiState?> =
        editorStateManager.editorState.mapState(viewModelScope) { editorState ->
            editorState.focusedNote?.let { note ->
                UiState(
                    currentNoteId = note.id,
                    currentNoteSource = note.source,
                    fontSizeSp = fontSizeProvider.current
                )
            }
        }

    fun updateSourceCode(source: Note.Source) {
        viewModelScope.launch {
            editorStateManager.updateFocusedNoteSourceCode(source)
            editorStateManager.updateLatestUpdatedDate()
            viewerStateManager.render()
        }
    }
}