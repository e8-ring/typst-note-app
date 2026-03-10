package com.mono9rome.typst_note_app.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mono9rome.typst_note_app.core.state.EditorStateManager
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.util.mapState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class EditorTabsViewModel(
    private val editorStateManager: EditorStateManager
) : ViewModel() {

    data class UiState(
        val openTabs: List<Note.Light>,
        val focusedNoteId: Note.Id,
    )

    val uiState: StateFlow<UiState?> =
        editorStateManager.editorState.mapState(viewModelScope) { editorState ->
            editorState.focusedNote?.let { note ->
                UiState(
                    openTabs = editorState.openNotes,
                    focusedNoteId = note.id
                )
            }
        }

    fun setFocus(noteId: Note.Id) {
        viewModelScope.launch { editorStateManager.setFocus(noteId) }
    }

    fun closeNote(note: Note.Light) {
        viewModelScope.launch { editorStateManager.closeNote(note) }
    }
}