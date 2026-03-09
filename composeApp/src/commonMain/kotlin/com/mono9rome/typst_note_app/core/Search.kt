package com.mono9rome.typst_note_app.core

import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class Search {

    typealias Filter<T> = (value: T, keywords: List<String>) -> Boolean

    suspend fun <T> run(
        list: List<T>,
        query: String,
        filter: Filter<T>,
    ): List<T> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext list

        val keywords = query.split(" ")

        list.filter { value -> filter(value, keywords) }
    }

    val noteFilter: Filter<Note.Medium> = { note, keywords ->
        // 全ての word in keywords に対して、title が word を部分文字列として含む
        val titleHit = keywords.all { word ->
            note.metadata.title?.value?.contains(word) ?: false
        }
        // 全ての word in keywords に対して、note のあるタグ tag が存在して、tag の名前が word を部分文字列として含む
        val tagHit = keywords.all { word ->
            note.metadata.tags.any { tagName -> tagName.value.contains(word) }
        }

        titleHit || tagHit
    }

    val tagFilter: Filter<Note.Tag> = { tag, keywords ->
        keywords.all { word -> tag.name.value.contains(word) }
    }
}