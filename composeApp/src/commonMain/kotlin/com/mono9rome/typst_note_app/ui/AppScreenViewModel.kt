package com.mono9rome.typst_note_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mono9rome.typst_note_app.core.state.EditorStateManager
import com.mono9rome.typst_note_app.core.state.NoteStateManager
import com.mono9rome.typst_note_app.core.state.SearchStateManager
import com.mono9rome.typst_note_app.core.state.TagStateManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class AppScreenViewModel(
    private val noteStateManager: NoteStateManager,
    private val tagStateManager: TagStateManager,
    private val searchStateManager: SearchStateManager,
    editorStateManager: EditorStateManager,
) : ViewModel() {

    val isSomeNoteFocused: StateFlow<Boolean> =
        editorStateManager.editorState.map { it.focusedNote != null }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )


    // アプリ全体で使うデータの読み込み処理
    init {
        viewModelScope.launch {
            noteStateManager.loadAll()
            tagStateManager.loadAll()
            searchStateManager.refreshNoteResult()
            searchStateManager.refreshTagResult()
            // TODO: セッションの復元機能つけたら、viewerStateManager.render() もする
        }
    }
}