package com.mono9rome.typst_note_app.data

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

    fun readText(fileName: String): String? {
        val path = "${storageDir.path}/$fileName".toPath()

        if (!FileSystem.SYSTEM.exists(path)) return null

        return FileSystem.SYSTEM.source(path).buffer().use { source -> source.readUtf8() }
    }
}