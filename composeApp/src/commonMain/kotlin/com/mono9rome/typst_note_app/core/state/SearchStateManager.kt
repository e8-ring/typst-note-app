package com.mono9rome.typst_note_app.core.state

import com.mono9rome.typst_note_app.core.Search
import com.mono9rome.typst_note_app.di.Singleton
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

@Inject
@Singleton
class SearchStateManager(
    private val noteStateManager: NoteStateManager,
    private val tagStateManager: TagStateManager,
    private val search: Search,
) {
    private val _noteSearchState = MutableStateFlow<SearchState<Note.Medium>>(SearchState.default())
    private val _tagSearchState = MutableStateFlow<SearchState<Note.Tag>>(SearchState.default())
    val noteSearchState: StateFlow<SearchState<Note.Medium>> = _noteSearchState.asStateFlow()
    val tagSearchState: StateFlow<SearchState<Note.Tag>> = _tagSearchState.asStateFlow()

    suspend fun refreshNoteResult() = runNoteSearch(
        query = noteSearchState.value.query,
    )

    suspend fun runNoteSearch(query: String) = _noteSearchState.runSearch(
        candidates = noteStateManager.allNotes.value,
        query = query,
        filter = search.noteFilter
    )

    suspend fun refreshTagResult() = runTagSearch(
        query = tagSearchState.value.query,
    )

    suspend fun runTagSearch(query: String) = _tagSearchState.runSearch(
        candidates = tagStateManager.allTags.value,
        query = query,
        filter = search.tagFilter
    )

    /* --- common private methods --- */

    private suspend fun <T> MutableStateFlow<SearchState<T>>.runSearch(
        candidates: List<T>,
        query: String,
        filter: Search.Filter<T>
    ) {
        this.updateQuery(query)
        this.update {
            it.copy(
                result = search.run(candidates, query, filter),
            )
        }
    }

    private fun <T> MutableStateFlow<SearchState<T>>.updateQuery(query: String) {
        this.update {
            it.copy(query = query)
        }
    }
}