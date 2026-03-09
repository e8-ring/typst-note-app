package com.mono9rome.typst_note_app.ui.state

import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.model.SourceCode
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

@Inject
class EditorStateManager {

    /* --- specialized methods --- */

    fun MainMutableStateFlow.addNoteToTab(note: Note.Light) =
        this.updateOpenNotes { it + listOf(note) }

    fun MainMutableStateFlow.clearCurrentNote() =
        this.updateCurrentNote { null }

    fun MainMutableStateFlow.updateCurrentNoteTitle(
        noteId: Note.Id,
        inputTitle: Note.Title
    ) {
        val title = if (inputTitle.isBlank()) null else inputTitle
        this.updateOpenNotes { list ->
            val openNotes = list.toMutableList()
            val currentNoteIndex = list.indexOfFirst { it.id == noteId }
            val currentNote = openNotes[currentNoteIndex]
            openNotes[currentNoteIndex] = currentNote.copy(title = title)

            openNotes
        }
        this.updateCurrentNote {
            it?.copy(
                metadata = it.metadata.copy(
                    title = title,
                )
            )
        }
    }

    fun MainMutableStateFlow.updateSourceCode(sourceCode: SourceCode) =
        this.updateCurrentNote {
            it?.copy(
                sourceCode = sourceCode,
            )
        }

    /* --- general methods --- */

    fun MainMutableStateFlow.updateOpenNotes(updateNotes: (List<Note.Light>) -> List<Note.Light>) {
        this.update {
            it.copy(
                editorState = it.editorState.copy(
                    openNotes = it.editorState.openNotes.let(updateNotes),
                )
            )
        }
    }

    fun MainMutableStateFlow.updateCurrentNote(updateNote: (Note?) -> Note?) {
        this.update {
            it.copy(
                editorState = it.editorState.copy(
                    currentNote = it.editorState.currentNote.let(updateNote)
                )
            )
        }
    }
}