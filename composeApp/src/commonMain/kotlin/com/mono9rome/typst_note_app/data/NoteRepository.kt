package com.mono9rome.typst_note_app.data

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.recover
import com.mono9rome.typst_note_app.core.Counter
import com.mono9rome.typst_note_app.model.Err
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import java.io.IOException

@Inject
class NoteRepository(
    private val fileManager: LocalFileManager,
    private val counter: Counter
) {

    private val json = Json { prettyPrint = true }

    /* -- Create -- */

    suspend fun makeNew(also: suspend (Note.Id) -> Unit = {}) {
        recover({
            // ノート id カウンターをインクリメントし、それを id に設定
            counter.increment()
            val newNoteId = counter.now()
            // ノートの実体ファイルを作成
            fileManager.makeFile(newNoteId.toFilePath())
            // ノートの初期メタデータを書き込み
            val newNoteMetadata = Note.Metadata.default
            updateMetadata(newNoteId) { newNoteMetadata }
            // 追加処理
            also(newNoteId)
        }) { e ->
            throw IOException(e.message)
        }
    }

    /* -- Read -- */

    suspend fun getAll(): List<Note.Medium> =
        fileManager.getAllFileNamesInDir(CONTENT_DIR_NAME)
            .map { fileName ->
                val noteId = Note.Id(fileName.removeTypExtension())
                Note.Medium(
                    id = noteId,
                    metadata = getMetadata(noteId),
                )
            }

    context(_: Raise<Err>)
    suspend fun get(noteId: Note.Id): Note = Note(
        id = noteId,
        metadata = getMetadata(noteId),
        source = getSourceCode(noteId),
    )

    suspend fun getMetadata(noteId: Note.Id): Note.Metadata {
        val metadataMap = readNoteMetadataMap()
        return metadataMap[noteId] ?: Note.Metadata.default
    }

    context(_: Raise<Err>)
    suspend fun getSourceCode(noteId: Note.Id): Note.Source =
        Note.Source(fileManager.readText(noteId.toFilePath()))

    private fun String.removeTypExtension(): String = this.dropLast(4)

    @Throws
    private suspend fun readNoteMetadataMap(): Note.MetaDataMap = withContext(Dispatchers.IO) {
        val jsonString = recover({ fileManager.readText(NOTE_METADATA_MAP_FILE_NAME) }) { e ->
            throw IOException("NoteRepository.readNoteMetadataMap: ${e.message}")
        }
        if (jsonString.isBlank()) return@withContext emptyMap()
        catch({ json.decodeFromString<Note.MetaDataMap>(jsonString) }) { e ->
            throw IOException("NoteRepository.readNoteMetadataMap: ${e.message}")
        }
    }

    /* -- Update -- */

    context(_: Raise<Err>)
    suspend fun updateTitle(
        noteId: Note.Id,
        inputTitle: Note.Title
    ) = updateMetadata(noteId) {
        val title = if (inputTitle.isBlank()) null else inputTitle
        it?.copy(
            title = title
        ) ?: Note.Metadata(
            title = title,
            tags = emptyList(),
        )
    }

    context(_: Raise<Err>)
    suspend fun addTag(
        noteId: Note.Id,
        tagId: Note.Tag.Id,
    ) = updateMetadata(noteId) {
        it?.copy(
            tags = it.tags + listOf(tagId)
        ) ?: Note.Metadata(
            title = null,
            tags = listOf(tagId),
        )
    }

    context(_: Raise<Err>)
    suspend fun deleteTag(
        noteId: Note.Id,
        tagId: Note.Tag.Id,
    ) = updateMetadata(noteId) {
        it?.copy(
            tags = it.tags - listOf(tagId).toSet()
        ) ?: Note.Metadata.default
    }

    context(_: Raise<Err>)
    private suspend fun updateMetadata(
        noteId: Note.Id,
        update: (Note.Metadata?) -> Note.Metadata
    ) {
        val metadataMap = readNoteMetadataMap().toMutableMap()
        val currentNoteMetadataOrNull = metadataMap[noteId]
        metadataMap[noteId] = update(currentNoteMetadataOrNull)
        writeNoteMetadataMap(metadataMap)
    }

    @Throws
    private suspend fun writeNoteMetadataMap(metaDataMap: Note.MetaDataMap) = withContext(Dispatchers.IO) {
        val jsonString = catch({ json.encodeToString(metaDataMap) }) { e ->
            throw IOException("NoteRepository.writeNoteMetadataMap: ${e.message}:")
        }
        fileManager.writeText(NOTE_METADATA_MAP_FILE_NAME, jsonString)
    }

    suspend fun write(noteId: Note.Id, content: String) {
        fileManager.writeText(noteId.toFilePath(), content)
    }

    /* -- Delete -- */


    /* -- Helper methods -- */

    private fun Note.Id.toFilePath(): String = "$CONTENT_DIR_NAME/${this.toFileName()}"

    private companion object {
        const val CONTENT_DIR_NAME = "content"
        const val NOTE_METADATA_MAP_FILE_NAME = "notes_metadata.json"
    }
}