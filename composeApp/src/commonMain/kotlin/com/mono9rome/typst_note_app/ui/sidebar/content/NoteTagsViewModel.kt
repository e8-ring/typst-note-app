package com.mono9rome.typst_note_app.ui.sidebar.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mono9rome.typst_note_app.core.state.SearchStateManager
import com.mono9rome.typst_note_app.core.state.TagStateManager
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class NoteTagsViewModel(
    private val tagStateManager: TagStateManager,
    private val searchStateManager: SearchStateManager
) : ViewModel() {

    val uiState = searchStateManager.tagSearchState

    fun load() {
        viewModelScope.launch {
            tagStateManager.loadAll()
            searchStateManager.refreshTagResult()
        }
    }

    fun addNewTag(tagName: Note.Tag.Name) {
        viewModelScope.launch { tagStateManager.addNewTag(tagName) }
    }

    fun runTagSearch(query: String) {
        viewModelScope.launch { searchStateManager.runTagSearch(query) }
    }
}