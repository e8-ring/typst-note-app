package com.mono9rome.typst_note_app.ui.sidebar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.raise.recover
import com.mono9rome.typst_note_app.core.Search
import com.mono9rome.typst_note_app.data.NoteRepository
import com.mono9rome.typst_note_app.data.TagRepository
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import java.io.IOException

@Inject
class SidebarViewModel(
    private val noteRepository: NoteRepository,
    private val tagRepository: TagRepository,
    private val search: Search
) : ViewModel() {

    data class UiState(
        val menuBarState: MenuBarContentType,
        val noteList: List<Note.Medium>,
        val tagList: List<Note.Tag>,
    ) {
        companion object {
            val default = UiState(
                menuBarState = MenuBarContentType.None,
                noteList = emptyList(),
                tagList = emptyList(),
            )
        }
    }

    data class SearchState<T>(
        val query: String,
        val result: List<T>
    ) {
        companion object {
            fun <T> default(): SearchState<T> = SearchState(
                query = "",
                result = emptyList()
            )
        }
    }

    /* --- properties --- */

    private val _uiState = MutableStateFlow<UiState>(UiState.default)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val notesManager = NotesManager()
    val noteSearchManager = SearchManager(
        getLatestList = { uiState.value.noteList },
        filter = search.noteFilter
    )
    val tagsManager = TagsManager()
    val tagSearchManager = SearchManager(
        getLatestList = { uiState.value.tagList },
        filter = search.tagFilter
    )

    /* --- methods --- */

    init {
        loadAll()
    }

    suspend fun loadAllNotes() {
        _uiState.update {
            it.copy(
                noteList = recover({ noteRepository.getAllNotes() }) { e ->
                    throw IOException("ノートリストの読み込みに失敗 (SidebarViewModel.loadAllNotes)\n[エラー内容] ${e.message}")
                }
            )
        }
    }

    suspend fun loadAllTags() {
        _uiState.update {
            it.copy(
                tagList = tagRepository.getAll()
            )
        }
    }

    private fun loadAll() {
        viewModelScope.launch {
            loadAllNotes()
            loadAllTags()
            noteSearchManager.run("")
            tagSearchManager.run("")
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
        loadAll()
    }


    inner class NotesManager {
        fun refresh() {
            viewModelScope.launch { loadAllNotes() }
        }

        fun addNewNote() {
            viewModelScope.launch {
                noteRepository.makeNew()
                loadAllNotes()
            }
        }
    }

    inner class TagsManager {
        fun refresh() {
            viewModelScope.launch {
                loadAllTags()
                tagSearchManager.run(tagSearchManager.searchState.value.query)
            }
        }

        fun addNewTag(tagName: Note.Tag.Name) {
            viewModelScope.launch {
                recover({ tagRepository.makeNew(tagName, null) }) { e ->
                    // TODO: エラーハンドリング（同名のタグが存在する場合）
                    throw IllegalArgumentException(e.message)
                }
                refresh()
            }
        }
    }

    inner class SearchManager<T>(
        private val getLatestList: () -> List<T>,
        private val filter: Search.Filter<T>
    ) {

        private val _searchState = MutableStateFlow(SearchState.default<T>())
        val searchState: StateFlow<SearchState<T>> = _searchState.asStateFlow()

        fun run(query: String) {
            updateQuery(query)
            viewModelScope.launch {
                _searchState.update {
                    it.copy(
                        result = search.run(getLatestList(), query, filter)
                    )
                }
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