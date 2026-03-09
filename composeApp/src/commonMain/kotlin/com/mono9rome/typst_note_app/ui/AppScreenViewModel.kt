package com.mono9rome.typst_note_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.raise.recover
import com.mono9rome.typst_note_app.data.NoteRepository
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.model.SourceCode
import com.mono9rome.typst_note_app.parser.BlockParser
import com.mono9rome.typst_note_app.ui.state.EditorStateManager
import com.mono9rome.typst_note_app.ui.state.MainUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class AppScreenViewModel(
    private val blockParser: BlockParser,
    private val noteRepository: NoteRepository,
    private val editorStateManager: EditorStateManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState.default)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

//    init {
//        _uiState.update {
//            // アップデート
//        }
//    }

    /* --- public methods --- */

    fun onSelectFileInChooser(noteId: Note.Id) {
        viewModelScope.launch {
            if (isNotYetOpen(noteId)) {
                val note = Note.Light(
                    id = noteId,
                    title = recover({ noteRepository.getMetadata(noteId).title }) { null }
                )
                with(editorStateManager) {
                    _uiState.addNoteToTab(note)
                }
            }
            onSelectNoteInTabs(noteId)
        }
    }

    private fun isNotYetOpen(noteId: Note.Id): Boolean = !(_uiState.value.editorState.openNotes.any { it.id == noteId })

    fun onSelectNoteInTabs(noteId: Note.Id) = with(editorStateManager) {
        viewModelScope.launch {
            val selectedNote = recover({ noteRepository.get(noteId) }) { e ->
                // 諦める
                println("onSelectNoteInTabs: ${e.message}")
                return@launch
            }
            _uiState.updateCurrentNote { selectedNote }
            render(selectedNote.sourceCode)
        }
    }

    fun closeNote(
        note: Note.Light,
        isFocused: Boolean
    ) {
        val openNotes = _uiState.value.editorState.openNotes
        val newOpenNotes = openNotes - note

        when (newOpenNotes.isEmpty()) {
            true -> initializeEditor()
            false -> {
                if (isFocused) {
                    val currentNoteIdIndex = openNotes.indexOfFirst { it.id == note.id }
                    val nextFocusedNoteIdIndex = when {
                        currentNoteIdIndex >= 1 -> currentNoteIdIndex.dec()
                        currentNoteIdIndex == 0 -> 1
                        else -> throw IndexOutOfBoundsException("at MainScreenViewModel::closeNote")
                    }
                    val nextFocusedNoteId = openNotes[nextFocusedNoteIdIndex].id

                    onSelectNoteInTabs(nextFocusedNoteId)
                }
            }
        }

        with(editorStateManager) { _uiState.updateOpenNotes { newOpenNotes } }
    }

    fun onTitleChange(
        noteId: Note.Id,
        title: Note.Title
    ) = with(editorStateManager) {
        _uiState.updateCurrentNoteTitle(noteId, title)
        viewModelScope.launch {
            recover({
                noteRepository.changeTitle(noteId, title)
            }) { e ->
                // TODO: エラーハンドリング
                println("Error: ${e.message}")
            }
        }
    }

    fun onEdited(sourceCode: SourceCode) = with(editorStateManager) {
        val currentNote = _uiState.value.editorState.currentNote
        check(currentNote != null)
        viewModelScope.launch {
            _uiState.updateSourceCode(sourceCode)
            render(sourceCode)
            noteRepository.write(currentNote.id, sourceCode.value)
        }
    }

    /* --- 状態更新 private methods --- */

    private fun initializeEditor() = with(editorStateManager) {
        _uiState.clearCurrentNote()
        clearViewer()
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