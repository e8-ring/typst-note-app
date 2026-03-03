package com.mono9rome.typst_note_app.model

data class Tag(val name: String)

data class NoteMetadata(
    val id: Id,
    val title: Title,
    val tags: List<Tag>,
) {
    data class Id(val value: String)
    @JvmInline value class Title(val value: String)
}
