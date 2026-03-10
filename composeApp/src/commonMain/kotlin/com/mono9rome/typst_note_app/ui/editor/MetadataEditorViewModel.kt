package com.mono9rome.typst_note_app.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mono9rome.typst_note_app.core.state.EditorStateManager
import com.mono9rome.typst_note_app.core.state.TagStateManager
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class MetadataEditorViewModel(
    tagStateManager: TagStateManager,
    private val editorStateManager: EditorStateManager,
) : ViewModel() {

    data class UiState(
        val currentNote: Note.Medium?,
        val allTags: List<Note.Tag.Basic>
    ) {
        val allTagsMap: Map<Note.Tag.Id, Note.Tag.Name> =
            allTags.associate { it.id to it.name }

        companion object {
            val default = UiState(
                currentNote = null,
                allTags = emptyList(),
            )
        }
    }

    val uiState: StateFlow<UiState> = combine(
        tagStateManager.allTags,
        editorStateManager.editorState
    ) { allTags, editorState ->
        UiState(
            currentNote = editorState.focusedNote?.toMedium(),
            allTags = allTags.map { it.toBasic() }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UiState.default
    )

    fun changeTitle(inputTitle: Note.Title) {
        viewModelScope.launch { editorStateManager.changeFocusedNoteTitle(inputTitle) }
    }

    fun attachTag(tagId: Note.Tag.Id) {
        viewModelScope.launch { editorStateManager.attachTagToFocusedNote(tagId) }
    }

    fun detachTag(tagId: Note.Tag.Id) {
        viewModelScope.launch { editorStateManager.detachTagFromFocusedNote(tagId) }
    }

    fun getTagById(tagId: Note.Tag.Id): Note.Tag.Basic = Note.Tag.Basic(
        name = uiState.value.allTagsMap[tagId]
            ?: throw IllegalStateException("タグデータが不整合的です。\nタグ id ${tagId.value} に対応するタグ名が取得できませんでした。"),
        id = tagId
    )
}