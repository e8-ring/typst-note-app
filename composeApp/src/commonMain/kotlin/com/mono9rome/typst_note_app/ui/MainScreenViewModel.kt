package com.mono9rome.typst_note_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.raise.recover
import com.mono9rome.typst_note_app.data.LocalFileManager
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.parser.BlockParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class MainScreenViewModel(
    private val blockParser: BlockParser,
    private val fileManager: LocalFileManager
) : ViewModel() {

    data class UiState(
        val sourceCode: SourceCode,
        val fontSizeSp: Float,
        val contentBlocks: List<ContentBlock>,
        val isCompileError: Boolean,
    ) {
        companion object {
            val default = UiState(
                sourceCode = SourceCode(
                    value = "",
                ),
                fontSizeSp = 14f,
                contentBlocks = listOf(),
                isCompileError = false
            )
        }
    }

    private val _uiState = MutableStateFlow(UiState.default)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
//        _uiState.update {
//            // アップデート
//        }
    }

    fun onEdited(sourceCode: SourceCode) {
        viewModelScope.launch {
            updateSourceCode(sourceCode)
            render(
                sourceCode = sourceCode,
                textSizeSp = _uiState.value.fontSizeSp,
            )
            fileManager.writeText("typstnoteapp1111.txt", sourceCode.value)
        }
    }

    private fun updateSourceCode(sourceCode: SourceCode) {
        _uiState.update {
            it.copy(
                sourceCode = sourceCode,
            )
        }
        println("updated source code!")
    }

    fun updateTextSizeSp(textSizeSp: Float?) = textSizeSp?.let {
        _uiState.update {
            it.copy(
                fontSizeSp = textSizeSp
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
        @Suppress("unused") textSizeSp: Float // parse 内でフォントサイズ参照するがここでは使ってない
    ) {
        println("Start render!")
        val contentBlocks = recover({ blockParser.parse(sourceCode.value) }) { null }
        contentBlocks?.let {
            clearCompileError()
            updateContents(it)
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

    private fun updateContents(contentBlocks: List<ContentBlock>) {
        _uiState.update {
            it.copy(
                contentBlocks = contentBlocks,
            )
        }
    }
}