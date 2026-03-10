package com.mono9rome.typst_note_app.data

import arrow.core.raise.Raise
import arrow.core.raise.context.ensure
import arrow.core.raise.context.raise
import com.mono9rome.typst_note_app.model.Err
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import java.io.FileNotFoundException
import kotlin.jvm.Throws

class AppStorageDir(val path: String)

@Inject
class LocalFileManager(private val storageDir: AppStorageDir) {

    /* -- Read -- */

    @Throws
    suspend fun getAllFileNamesInDir(dirName: String): List<String> = withContext(Dispatchers.IO) {
        FileSystem.SYSTEM.listOrNull(dirName.toAbsPath())?.map { it.name }
            ?: throw FileNotFoundException("FileManager.getAllFileNamesInDir : $dirName ディレクトリ内のファイルが 1 つも取得できませんでした。")
    }

    context(_: Raise<Err>)
    suspend fun readText(path: String): String = withContext(Dispatchers.IO) {
        val okioPath = path.toAbsPath()

        ensure(FileSystem.SYSTEM.exists(okioPath)) {
            raise(Err("Not Found: $path"))
        }

        FileSystem.SYSTEM.source(okioPath).buffer().use { source -> source.readUtf8() }
    }

    /* -- Write -- */

    suspend fun makeFile(path: String) = writeText(path, "")

    suspend fun writeText(
        path: String,
        content: String
    ) = withContext(Dispatchers.IO) {
        FileSystem.SYSTEM.sink(path.toAbsPath()).buffer().use { sink ->
            sink.writeUtf8(content)
        }
    }

    /* -- Helper methods -- */

    private fun String.toAbsPath(): Path = "${storageDir.path}/$this".toPath()
}