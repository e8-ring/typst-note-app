package com.mono9rome.typst_note_app.ui.sidebar.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mono9rome.typst_note_app.core.state.EditorStateManager
import com.mono9rome.typst_note_app.core.state.NoteStateManager
import com.mono9rome.typst_note_app.core.state.SearchStateManager
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class NoteChooserViewModel(
    private val noteStateManager: NoteStateManager,
    private val editorStateManager: EditorStateManager,
    private val searchStateManager: SearchStateManager
) : ViewModel() {

    val uiState = searchStateManager.noteSearchState

    fun load() {
        viewModelScope.launch {
            noteStateManager.loadAll()
            searchStateManager.refreshNoteResult()
        }
    }

    fun addNewNote() {
        viewModelScope.launch { noteStateManager.addNewNote() }
    }

    fun runNoteSearch(query: String) {
        viewModelScope.launch { searchStateManager.runNoteSearch(query) }
    }

    fun setFocusNote(noteId: Note.Id) {
        viewModelScope.launch { editorStateManager.setFocus(noteId) }
    }
}