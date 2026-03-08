package com.mono9rome.typst_note_app.data

import arrow.core.raise.Raise
import arrow.core.raise.context.ensure
import arrow.core.raise.context.raise
import com.mono9rome.typst_note_app.model.Err
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import okio.Path.Companion.toPath
import okio.FileSystem
import okio.buffer

class AppStorageDir(val path: String)

@Inject
class LocalFileManager(private val storageDir: AppStorageDir) {

    fun getAll(): List<String>? =
        FileSystem.SYSTEM.listOrNull(storageDir.path.toPath())?.map { it.name }

    fun writeText(fileName: String, content: String) {
        val path = "${storageDir.path}/$fileName".toPath()

        FileSystem.SYSTEM.sink(path).buffer().use { sink ->
            sink.writeUtf8(content)
        }
    }

    context(_: Raise<Err>)
    suspend fun readText(fileName: String): String = withContext(Dispatchers.IO) {
        val path = "${storageDir.path}/$fileName".toPath()

        ensure(FileSystem.SYSTEM.exists(path)) {
            raise(Err("Not Found: $fileName"))
        }

        return@withContext FileSystem.SYSTEM.source(path).buffer().use { source -> source.readUtf8() }
    }
}