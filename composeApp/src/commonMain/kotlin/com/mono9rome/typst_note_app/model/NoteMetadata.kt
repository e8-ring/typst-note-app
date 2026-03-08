package com.mono9rome.typst_note_app.model

import com.mono9rome.typst_note_app.ui.SourceCode

data class Note(
    val metadata: Metadata,
    val sourceCode: SourceCode,
) {
    data class Metadata(
        val id: Id,
        val title: Title?,
        val tags: List<Tag>,
    ) {
        val fileName: String = "${id.value}.typ"
    }

    data class Light(
        val id: Id,
        val title: Title?,
    )

    data class Id(val value: String) {
        fun toFileName(): String = "$value.typ"
    }
    @JvmInline value class Title(val value: String)
    data class Tag(val name: String)
}




