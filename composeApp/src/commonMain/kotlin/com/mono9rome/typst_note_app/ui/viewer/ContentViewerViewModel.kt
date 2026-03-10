package com.mono9rome.typst_note_app.ui.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mono9rome.typst_note_app.FontSizeProvider
import com.mono9rome.typst_note_app.core.state.EditorStateManager
import com.mono9rome.typst_note_app.core.state.ViewerState
import com.mono9rome.typst_note_app.core.state.ViewerStateManager
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

@Inject
class ContentViewerViewModel(
    private val fontSizeProvider: FontSizeProvider,
    editorStateManager: EditorStateManager,
    viewerStateManager: ViewerStateManager,
) : ViewModel() {

    data class UiState(
        val currentNoteId: Note.Id,
        val viewerState: ViewerState,
        val fontSizeSp: Float
    )

    val uiState: StateFlow<UiState?> = combine(
        editorStateManager.editorState,
        viewerStateManager.viewerState
    ) { editorState, viewerState ->
        editorState.focusedNote?.let { note ->
            UiState(
                currentNoteId = note.id,
                viewerState = viewerState,
                fontSizeSp = fontSizeProvider.current
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
}