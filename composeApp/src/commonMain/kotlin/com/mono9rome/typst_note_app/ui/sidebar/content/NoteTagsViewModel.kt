package com.mono9rome.typst_note_app.ui.sidebar.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mono9rome.typst_note_app.core.Search
import com.mono9rome.typst_note_app.core.state.SearchStateManager
import com.mono9rome.typst_note_app.core.state.TagStateManager
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class NoteTagsViewModel(
    private val tagStateManager: TagStateManager,
    searchStateManager: SearchStateManager,
    search: Search
) : ViewModel() {

    private val _query: MutableStateFlow<String> = MutableStateFlow("")

    val uiState: StateFlow<List<Note.Tag>> = searchStateManager.stream(
        listFlow = tagStateManager.allTags,
        queryFlow = _query,
        filter = search.tagFilter
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun load() {
        viewModelScope.launch { tagStateManager.loadAll() }
    }

    fun addNewTag(tagName: Note.Tag.Name) {
        viewModelScope.launch { tagStateManager.addNewTag(tagName) }
    }

    fun runTagSearch(query: String) {
        _query.update { query }
    }

    fun renameTag(tagId: Note.Tag.Id, newName: String) {
        viewModelScope.launch { tagStateManager.renameTag(tagId, newName) }
    }
}