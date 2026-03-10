package com.mono9rome.typst_note_app.core.state

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