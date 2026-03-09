package com.mono9rome.typst_note_app.core

import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class Search {
    suspend fun run(
        notes: List<Note.Medium>,
        query: String
    ): List<Note.Medium> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()

        println("notes: $notes")

        val keywords = query.split(" ")
        println("keywords: $keywords")

        notes.filter { note ->
            // 全ての word in keywords に対して、title が word を部分文字列として含む
            val titleHit = keywords.all { word ->
                note.metadata.title?.value?.contains(word) ?: false
            }
            // 全ての word in keywords に対して、note のあるタグ tag が存在して、tag が word を部分文字列として含む
            val tagHit = keywords.all { word ->
                note.metadata.tags.any { tag -> tag.name.contains(word) }
            }

            titleHit || tagHit
        }
    }
}