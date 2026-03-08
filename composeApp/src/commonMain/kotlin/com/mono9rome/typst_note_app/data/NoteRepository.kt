package com.mono9rome.typst_note_app.data

import arrow.core.raise.Raise
import com.mono9rome.typst_note_app.model.Err
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.SourceCode
import me.tatarka.inject.annotations.Inject

@Inject
class NoteRepository(
    private val fileManager: LocalFileManager
) {
    context(_: Raise<Err>)
    suspend fun get(noteId: Note.Id) : Note {
        val fileName = noteId.toFileName()
        val sourceText = fileManager.readText(fileName)
        return Note(
            metadata = Note.Metadata(
                id = noteId,
                title = null,
                tags = emptyList(),
            ),
            sourceCode = SourceCode(sourceText)
        )
    }
}