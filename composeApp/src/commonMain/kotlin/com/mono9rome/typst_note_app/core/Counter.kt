package com.mono9rome.typst_note_app.core

import arrow.core.raise.Raise
import arrow.core.raise.context.ensure
import arrow.core.raise.context.raise
import com.mono9rome.typst_note_app.data.LocalFileManager
import com.mono9rome.typst_note_app.model.Err
import com.mono9rome.typst_note_app.model.Note
import me.tatarka.inject.annotations.Inject

@Inject
class Counter(
    private val fileManager: LocalFileManager
) {
    context(_: Raise<Err>)
    suspend fun now(): Note.Id {
        val readValue = fileManager.readText(COUNTER_FILE_NAME)
        ensure(readValue.toIntOrNull(RADIX) != null) {
            raise(Err("カウンターの値が不正です。\nat Counter.now"))
        }
        ensure(readValue.length == 4) {
            raise(Err("カウンターの桁数が 4 桁ではありません。\nat Counter.now"))
        }
        return Note.Id(readValue)
    }

    context(_: Raise<Err>)
    suspend fun increment() =
        fileManager.writeText(
            path = COUNTER_FILE_NAME,
            content = now().value
                .toInt(RADIX)
                .inc()
                .toString(RADIX)
                .uppercase()
                .padStart(4, '0')
        )

    private companion object {
        const val RADIX = 36
        const val COUNTER_FILE_NAME = "counter"
    }
}