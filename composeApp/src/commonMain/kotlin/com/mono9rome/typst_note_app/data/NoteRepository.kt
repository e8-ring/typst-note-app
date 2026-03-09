package com.mono9rome.typst_note_app.data

import arrow.core.raise.Raise
import arrow.core.raise.recover
import com.mono9rome.typst_note_app.core.Counter
import com.mono9rome.typst_note_app.model.Err
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.model.SourceCode
import me.tatarka.inject.annotations.Inject
import java.io.IOException

@Inject
class NoteRepository(
    private val fileManager: LocalFileManager,
    private val counter: Counter
) {
    context(_: Raise<Err>)
    suspend fun get(noteId: Note.Id): Note = Note(
        id = noteId,
        metadata = getMetadata(noteId),
        sourceCode = getSourceCode(noteId),
    )

    context(_: Raise<Err>)
    suspend fun getMetadata(noteId: Note.Id): Note.Metadata {
        val metadataMap = fileManager.readNoteMetadataMap()
        return metadataMap[noteId]?.let { metadata ->
            Note.Metadata(
                title = metadata.title,
                tags = emptyList()
            )
        } ?: Note.Metadata.default
    }

    context(_: Raise<Err>)
    suspend fun getSourceCode(noteId: Note.Id): SourceCode =
        SourceCode(fileManager.readText(noteId.toFilePath()))

    suspend fun write(noteId: Note.Id, content: String) {
        fileManager.writeText(noteId.toFilePath(), content)
    }

    suspend fun makeNew() {
        recover({
            counter.increment()
            val newNoteId = counter.now()
            fileManager.makeFile(newNoteId.toFilePath())
        }) { e ->
            throw IOException(e.message)
        }
    }

    private fun Note.Id.toFilePath(): String = "$CONTENT_DIR_NAME/${this.toFileName()}"

    context(_: Raise<Err>)
    suspend fun changeTitle(
        noteId: Note.Id,
        title: Note.Title
    ) {
        val metadataMap = fileManager.readNoteMetadataMap().toMutableMap()
        val currentNoteMetadata = metadataMap[noteId]
        metadataMap[noteId] = currentNoteMetadata?.copy(
            title = title
        ) ?: Note.Metadata(
            title = title,
            tags = emptyList(),
        )
        fileManager.writeNoteMetadataMap(metadataMap)
    }

    private companion object {
        const val CONTENT_DIR_NAME = "content"
    }
}