package com.mono9rome.typst_note_app.ui.sidebar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.raise.recover
import com.mono9rome.typst_note_app.core.Search
import com.mono9rome.typst_note_app.data.NoteRepository
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class SidebarViewModel(
    private val noteRepository: NoteRepository,
    private val search: Search
) : ViewModel() {

    data class UiState(
        val menuBarState: MenuBarContentType,
        val noteList: List<Note.Medium>
    ) {
        companion object {
            val default = UiState(
                menuBarState = MenuBarContentType.None,
                noteList = emptyList()
            )
        }
    }

    data class SearchState(
        val query: String,
        val result: List<Note.Medium>
    ) {
        companion object {
            val default = SearchState(
                query = "",
                result = emptyList()
            )
        }
    }

    /* --- properties --- */

    private val _uiState = MutableStateFlow<UiState>(UiState.default)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val notesManager = NotesManager()
    val searchManager = SearchManager()

    /* --- methods --- */

    init {
        loadAllNotes()
    }

    fun loadAllNotes() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    noteList = recover({ noteRepository.getAllNotes() }) {
                        emptyList()
                    }
                )
            }
        }
    }

    fun openMenuBarContent(contentType: MenuBarContentType) {
        _uiState.update {
            it.copy(
                menuBarState = when (it.menuBarState) {
                    contentType -> MenuBarContentType.None
                    else -> contentType
                }
            )
        }
        // 切り替えるたびに再読み込みする
        loadAllNotes()
    }


    inner class NotesManager {
        fun refresh() = loadAllNotes()

        fun addNewNote() {
            viewModelScope.launch {
                noteRepository.makeNew()
                loadAllNotes()
            }
        }
    }

    inner class SearchManager {

        private val _searchState = MutableStateFlow(SearchState.default)
        val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

        fun run(query: String) {
            updateQuery(query)
            viewModelScope.launch {
                _searchState.update {
                    it.copy(
                        result = search.run(uiState.value.noteList, query)
                    )
                }
                println(_searchState.value.result)
            }
        }

        private fun updateQuery(query: String) {
            _searchState.update {
                it.copy(
                    query = query
                )
            }
        }
    }
}