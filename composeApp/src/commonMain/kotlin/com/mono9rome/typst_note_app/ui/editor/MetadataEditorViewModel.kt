package com.mono9rome.typst_note_app.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mono9rome.typst_note_app.data.TagRepository
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class MetadataEditorViewModel(
    private val tagRepository: TagRepository
) : ViewModel() {

    data class UiState(
        val tagNameList: List<Note.Tag.Name>
    )

    private val _uiState = MutableStateFlow(UiState(emptyList()))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    tagNameList = tagRepository.getAll().map(Note.Tag::name),
                )
            }
        }
    }
}