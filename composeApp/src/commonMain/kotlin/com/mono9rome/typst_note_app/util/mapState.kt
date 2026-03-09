package com.mono9rome.typst_note_app.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

inline fun <T, R> StateFlow<T>.mapState(
    scope: CoroutineScope,
    crossinline transform: (T) -> R
): StateFlow<R> {
    return map { transform(it) }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = transform(this.value) // 元の value を使って自動で初期値を計算！
        )
}