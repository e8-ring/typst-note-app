package com.mono9rome.typst_note_app.core.state

import com.mono9rome.typst_note_app.model.Note

data class EditorState(
    val openNotes: List<Note.Light>,
    val focusedNote: Note?
) {
    companion object {
        val default = EditorState(
            openNotes = listOf(),
            focusedNote = null
        )
    }
}