package com.mono9rome.typst_note_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.render.MathRenderer
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

data class SourceCode(val value: String)

@Inject
class NoteFieldViewModel(
    private val mathRenderer: MathRenderer
) : ViewModel() {

    data class UiState(
        val sourceCode: SourceCode,
        val textSizeSp: Float,
        val contentBlocks: List<ContentBlock>,
        val isCompileError: Boolean,
    ) {
        companion object {
            val default = UiState(
                sourceCode = SourceCode(""),
                textSizeSp = 18f,
                contentBlocks = emptyList(),
                isCompileError = false,
            )
        }
    }

    private val _uiState = MutableStateFlow(UiState.default)
    val uiState: StateFlow<UiState> = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.default
        )

    // TODO : データの永続化したら init で _uiState.update する

    fun onEdited(sourceCode: SourceCode) {
        viewModelScope.launch {
            updateSourceCode(sourceCode)
            render(
                sourceCode = sourceCode,
                textSizeSp = _uiState.value.textSizeSp,
            )
        }
    }

    private fun updateSourceCode(sourceCode: SourceCode) {
        _uiState.update {
            it.copy(
                sourceCode = sourceCode,
            )
        }
    }

    fun updateTextSizeSp(textSizeSp: Float?) = textSizeSp?.let {
        _uiState.update {
            it.copy(
                textSizeSp = textSizeSp
            )
        }
        viewModelScope.launch {
            render(
                sourceCode = _uiState.value.sourceCode,
                textSizeSp = textSizeSp,
            )
        }
    }

    private suspend fun render(
        sourceCode: SourceCode,
        textSizeSp: Float
    ) {
        val outputContent = mathRenderer.renderToPng(sourceCode, textSizeSp)
        outputContent?.let {
            clearCompileError()
            updateOutputContent(it)
        } ?: {
            raiseCompileError()
        }
    }

    private fun raiseCompileError() {
        _uiState.update {
            it.copy(
                isCompileError = true
            )
        }
    }

    private fun clearCompileError() {
        _uiState.update {
            it.copy(
                isCompileError = false
            )
        }
    }

    private fun updateOutputContent(contentBlocks: List<ContentBlock>) {
        _uiState.update {
            it.copy(
                contentBlocks = contentBlocks,
            )
        }
    }
}