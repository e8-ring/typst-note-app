package com.mono9rome.typst_note_app.ui.sidebar.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mono9rome.typst_note_app.core.Search
import com.mono9rome.typst_note_app.core.state.EditorStateManager
import com.mono9rome.typst_note_app.core.state.NoteStateManager
import com.mono9rome.typst_note_app.core.state.SearchStateManager
import com.mono9rome.typst_note_app.core.state.ViewerStateManager
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class NoteChooserViewModel(
    private val noteStateManager: NoteStateManager,
    private val editorStateManager: EditorStateManager,
    private val viewerStateManager: ViewerStateManager,
    searchStateManager: SearchStateManager,
    search: Search
) : ViewModel() {

    private val _query: MutableStateFlow<String> = MutableStateFlow("")

    val uiState: StateFlow<List<Note.Medium>> = searchStateManager.stream(
        listFlow = noteStateManager.allNotes,
        queryFlow = _query,
        filter = search.noteFilter
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun load() {
        viewModelScope.launch {
            noteStateManager.loadAll()
        }
    }

    fun addNewNote() {
        viewModelScope.launch { noteStateManager.addNewNote() }
    }

    fun runNoteSearch(query: String) {
        _query.update { query }
    }

    fun setFocusNote(noteId: Note.Id) {
        viewModelScope.launch {
            editorStateManager.setFocus(noteId)
            viewerStateManager.render()
        }
    }
}