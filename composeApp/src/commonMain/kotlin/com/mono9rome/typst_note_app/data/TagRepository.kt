package com.mono9rome.typst_note_app.data

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.context.ensure
import arrow.core.raise.context.raise
import arrow.core.raise.recover
import com.mono9rome.typst_note_app.model.Err
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import java.io.FileNotFoundException
import java.io.IOException

@Inject
class TagRepository(
    private val fileManager: LocalFileManager
) {

    private val json = Json { prettyPrint = true }

    /* -- Create -- */

    @Throws
    context(_: Raise<Err>)
    suspend fun makeNew(
        name: Note.Tag.Name,
        description: String?,
    ) = withContext(Dispatchers.IO) {
        val allTags = getAll()
        val isNotConflict = allTags.all { it.name != name }
        ensure(isNotConflict) {
            raise(Err("同名のタグが既に存在します。"))
        }
        val newTag = Note.Tag(name, description)
        writeJson(allTags + newTag)
    }

    /* -- Read -- */

    @Throws
    suspend fun getAll(): List<Note.Tag> = withContext(Dispatchers.IO) {
        val jsonString = recover( { fileManager.readText(TAG_LIST_FILE_NAME) }) { e ->
            throw FileNotFoundException("TagRepository.getAll: ${e.message}")
        }
        if (jsonString.isBlank()) return@withContext emptyList()
        catch({ json.decodeFromString<List<Note.Tag>>(jsonString) }) { e ->
            throw IOException("TagRepository.getAll: ${e.javaClass.simpleName} : ${e.message}")
        }
    }

    /* -- Update -- */

    private suspend fun writeJson(tags: List<Note.Tag>) {
        val jsonString = json.encodeToString(tags)
        fileManager.writeText(TAG_LIST_FILE_NAME, jsonString)
    }

    @Throws
    suspend fun renameTag(tagId: Note.Tag.Id, newName: String) = withContext(Dispatchers.IO) {
        val allTags = getAll().toMutableList()
        val targetTagIndex = allTags.indexOfFirst { it.id == tagId }
        val targetTag = allTags[targetTagIndex]
        allTags[targetTagIndex] = targetTag.copy(
            name = Note.Tag.Name(newName)
        )
        writeJson(allTags)
    }

    private companion object {
        const val TAG_LIST_FILE_NAME = "tags.json"
    }
}