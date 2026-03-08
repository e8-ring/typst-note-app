package com.mono9rome.typst_note_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.raise.recover
import com.mono9rome.typst_note_app.data.LocalFileManager
import com.mono9rome.typst_note_app.data.NoteRepository
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.model.Note
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
    private val fileManager: LocalFileManager,
    private val noteRepository: NoteRepository,
) : ViewModel() {

    data class EditorState(
        val openNoteIds: List<Note.Id>,
        val currentNote: Note?
    ) {
        companion object {
            val default = EditorState(
                openNoteIds = listOf(),
                currentNote = null
            )
        }
    }

    data class UiState(
        val editorState: EditorState,
        val fontSizeSp: Float,
        val currentRenderedContent: List<ContentBlock>,
        val isCompileError: Boolean,
    ) {
        companion object {
            val default = UiState(
                editorState = EditorState.default,
                fontSizeSp = 14f,
                currentRenderedContent = listOf(),
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

    /* --- public methods --- */

    fun onSelectFileInChooser(noteId: Note.Id) {
        viewModelScope.launch {
            addNoteToTab(noteId)
            val selectedNote = recover({ noteRepository.get(noteId) }) { e ->
                // 諦める
                println("Error: ${e.message}")
                return@launch
            }
            updateCurrentNoteThenRender(selectedNote)
        }
    }

    fun onSelectNoteInTabs(noteId: Note.Id) {
        viewModelScope.launch {
            val selectedNote = recover({ noteRepository.get(noteId) }) { e ->
                // 諦める
                println("onSelectNoteInTabs: ${e.message}")
                return@launch
            }
            updateCurrentNoteThenRender(selectedNote)
        }
    }

    fun closeNote(
        noteId: Note.Id,
        isFocused: Boolean
    ) {
        val openNoteIds = _uiState.value.editorState.openNoteIds
        val newOpenNoteIds = openNoteIds - noteId

        when (newOpenNoteIds.isEmpty()) {
            true -> {
                clearCurrentNote()
                clearViewer()
            }
            false -> {
                if (isFocused) {
                    val currentNoteIdIndex = openNoteIds.indexOfFirst { it == noteId }
                    val nextFocusedNoteIdIndex = when {
                        currentNoteIdIndex >= 1 -> currentNoteIdIndex.dec()
                        currentNoteIdIndex == 0 -> 1
                        else -> throw IndexOutOfBoundsException("at MainScreenViewModel::closeNote")
                    }
                    val nextFocusedNoteId = openNoteIds[nextFocusedNoteIdIndex]

                    onSelectNoteInTabs(nextFocusedNoteId)
                }
            }
        }

        updateOpenNoteIds(newOpenNoteIds)
    }

    fun onEdited(sourceCode: SourceCode) {
        val currentNote = _uiState.value.editorState.currentNote
        check(currentNote != null)
        viewModelScope.launch {
            updateSourceCode(sourceCode)
            render(sourceCode)
            fileManager.writeText(currentNote.metadata.fileName, sourceCode.value)
        }
    }

    /* --- 状態更新 private methods --- */

    private fun addNoteToTab(noteId: Note.Id) {
        updateOpenNoteIds(_uiState.value.editorState.openNoteIds + listOf(noteId))
    }

    private fun updateOpenNoteIds(noteIds: List<Note.Id>) {
        _uiState.update {
            it.copy(
                editorState = it.editorState.copy(
                    openNoteIds = noteIds
                )
            )
        }
    }

    private suspend fun updateCurrentNoteThenRender(note: Note) {
        _uiState.update {
            it.copy(
                editorState = it.editorState.copy(
                    currentNote = note
                )
            )
        }
        render(note.sourceCode)
    }

    private fun clearCurrentNote() {
        _uiState.update {
            it.copy(
                editorState = it.editorState.copy(
                    currentNote = null
                )
            )
        }
    }

    private fun updateSourceCode(sourceCode: SourceCode) {
        _uiState.update {
            it.copy(
                editorState = it.editorState.copy(
                    currentNote = it.editorState.currentNote?.copy(
                        sourceCode = sourceCode,
                    )
                )
            )
        }
    }

    fun updateTextSizeSp(textSizeSp: Float?) = textSizeSp?.let {
        _uiState.update {
            it.copy(
                fontSizeSp = textSizeSp
            )
        }
    }

    private suspend fun render(sourceCode: SourceCode) {
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
                currentRenderedContent = contentBlocks,
            )
        }
    }

    private fun clearViewer() {
        _uiState.update {
            it.copy(
                currentRenderedContent = emptyList()
            )
        }
    }
}