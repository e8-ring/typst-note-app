package com.mono9rome.typst_note_app.ui.state

import com.mono9rome.typst_note_app.model.Note

data class EditorState(
    val openNotes: List<Note.Light>,
    val currentNote: Note?
) {
    companion object {
        val default = EditorState(
            openNotes = listOf(),
            currentNote = null
        )
    }
}