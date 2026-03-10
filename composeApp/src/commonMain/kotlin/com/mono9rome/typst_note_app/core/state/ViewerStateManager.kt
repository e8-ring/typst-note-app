package com.mono9rome.typst_note_app.core.state

import arrow.core.raise.recover
import com.mono9rome.typst_note_app.core.parser.BlockParser
import com.mono9rome.typst_note_app.di.Singleton
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.model.SourceCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

@Inject
@Singleton
class ViewerStateManager(
    private val blockParser: BlockParser,
    private val editorStateManager: EditorStateManager
) {

    private val _viewerState = MutableStateFlow(ViewerState.default)
    val viewerState: StateFlow<ViewerState> = _viewerState.asStateFlow()

    /**
     * 現在フォーカスされているノートの内容をビューアに描画する。
     * */
    suspend fun render() =
        editorStateManager.editorState.value.focusedNote?.let { note ->
            update(note.sourceCode)
        }

    private suspend fun update(sourceCode: SourceCode) {
        val contentBlocks = recover({ blockParser.parse(sourceCode.value) }) { null }
        contentBlocks?.let {
            clearCompileError()
            updateContents(it)
        } ?: {
            raiseCompileError()
        }
    }

    private fun raiseCompileError() {
        _viewerState.update {
            it.copy(
                isCompileError = true
            )
        }
    }

    private fun clearCompileError() {
        _viewerState.update {
            it.copy(
                isCompileError = false
            )
        }
    }

    private fun updateContents(contentBlocks: List<ContentBlock>) {
        _viewerState.update {
            it.copy(
                contentBlocks = contentBlocks,
            )
        }
    }
}