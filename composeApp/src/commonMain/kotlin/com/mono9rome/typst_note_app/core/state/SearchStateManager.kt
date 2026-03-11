package com.mono9rome.typst_note_app.core.state

import com.mono9rome.typst_note_app.core.Search
import com.mono9rome.typst_note_app.di.Singleton
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

@Inject
@Singleton
class SearchStateManager(
    private val search: Search,
) {
    fun <T> stream(
        listFlow: Flow<List<T>>,
        queryFlow: Flow<String>,
        filter: Search.Filter<T>,
    ): Flow<List<T>> = combine(listFlow, queryFlow) { list, query ->
        search.run(list, query, filter)
    }
}