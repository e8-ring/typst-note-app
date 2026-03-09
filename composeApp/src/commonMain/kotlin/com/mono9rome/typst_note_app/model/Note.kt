package com.mono9rome.typst_note_app.model

import kotlinx.serialization.Serializable
data class SourceCode(val value: String)

data class Note(
    val id: Id,
    val metadata: Metadata,
    val sourceCode: SourceCode,
) {
    data class Light(
        val id: Id,
        val title: Title?,
    )

    data class Medium(
        val id: Id,
        val metadata: Metadata
    ) {
        fun toLight(): Light = Light(
            id = id,
            title = metadata.title,
        )
    }

    @Serializable
    data class Metadata(
        val title: Title?,
        val tags: List<Tag>,
    ) {
        companion object {
            val default = Metadata(
                title = null,
                tags = emptyList()
            )
        }
    }
    typealias MetaDataMap = Map<Id, Metadata>

    @Serializable
    @JvmInline
    value class Id(val value: String) {
        fun toFileName(): String = "$value.typ"
    }

    @Serializable
    @JvmInline
    value class Title(val value: String) {
        fun isBlank(): Boolean = value.isBlank()
    }

    @Serializable
    data class Tag(val name: String)
}




