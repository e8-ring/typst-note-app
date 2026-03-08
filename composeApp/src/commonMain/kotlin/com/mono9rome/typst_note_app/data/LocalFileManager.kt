package com.mono9rome.typst_note_app.data

import arrow.core.raise.Raise
import arrow.core.raise.context.ensure
import arrow.core.raise.context.raise
import com.mono9rome.typst_note_app.model.Err
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer

class AppStorageDir(val path: String)

@Inject
class LocalFileManager(private val storageDir: AppStorageDir) {

    private val json = Json { prettyPrint = true }

    private val contentDir = "${storageDir.path}/$CONTENT_DIR_NAME"

    fun getAll(): List<String>? =
        FileSystem.SYSTEM.listOrNull(contentDir.toPath())?.map { it.name }

    suspend fun makeFile(path: String) = writeText(path, "")

    suspend fun writeText(
        path: String,
        content: String
    ) = withContext(Dispatchers.IO) {
        FileSystem.SYSTEM.sink(path.toAbsPath()).buffer().use { sink ->
            sink.writeUtf8(content)
        }
    }

    context(_: Raise<Err>)
    suspend fun readText(path: String): String = withContext(Dispatchers.IO) {
        val okioPath = path.toAbsPath()

        ensure(FileSystem.SYSTEM.exists(okioPath)) {
            raise(Err("Not Found: $path"))
        }

        FileSystem.SYSTEM.source(okioPath).buffer().use { source -> source.readUtf8() }
    }

    context(_: Raise<Err>)
    suspend fun writeNoteMetadataMap(metadataMap: Note.MetaDataMap) = withContext(Dispatchers.IO) {
        val path = "${storageDir.path}/$NOTE_METADATA_MAP_FILE_NAME".toPath()

        ensure(FileSystem.SYSTEM.exists(path)) {
            raise(Err("Not Found: $NOTE_METADATA_MAP_FILE_NAME"))
        }

        FileSystem.SYSTEM.sink(path).buffer().use { sink ->
            val jsonString = json.encodeToString(metadataMap)
            sink.writeUtf8(jsonString)
        }
    }

    // TODO: NoteRepository に移動すべき
    suspend fun readNoteMetadataMap(): Note.MetaDataMap = withContext(Dispatchers.IO) {
        val path = "${storageDir.path}/$NOTE_METADATA_MAP_FILE_NAME".toPath()

        if(!FileSystem.SYSTEM.exists(path)) {
            println("Not Found: $NOTE_METADATA_MAP_FILE_NAME")
            return@withContext emptyMap()
        }

        FileSystem.SYSTEM.source(path).buffer().use { source ->
            val jsonString = source.readUtf8()
            if (jsonString.isBlank()) return@use emptyMap()
            json.decodeFromString<Note.MetaDataMap>(jsonString)
        }
    }

    private fun String.toAbsPath(): Path = "${storageDir.path}/$this".toPath()

    private companion object {
        const val CONTENT_DIR_NAME = "content"
        const val NOTE_METADATA_MAP_FILE_NAME = "notes_metadata.json"
    }
}