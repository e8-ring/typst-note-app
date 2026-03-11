package com.mono9rome.typst_note_app.model

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID



data class Note(
    val id: Id,
    val metadata: Metadata,
    val source: Source,
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

    fun toMedium(): Medium = Medium(
        id = id,
        metadata = metadata,
    )

    @Serializable
    data class Metadata(
        val title: Title?,
        val tags: List<Tag.Id>,
        val createdDate: Timestamp = Timestamp.now(),
        val lastUpdatedDate: Timestamp? = null
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
    data class Tag(
        val name: Name,
        val description: String?,
        val id: Id = UUID.randomUUID().toString().let(::Id),
    ) {
        fun toBasic(): Basic = Basic(
            name = name,
            id = id
        )

        data class Basic(
            val name: Name,
            val id: Id
        )
        @Serializable
        @JvmInline
        value class Name(val value: String)

        @Serializable
        @JvmInline
        value class Id(val value: String)
    }

    @Serializable
    @JvmInline
    value class Timestamp(val value: Long) {
        fun format(pattern: String): String =
            SimpleDateFormat(pattern, Locale.JAPAN).format(value)

        companion object {
            const val PATTERN_DAY_SLASHED = "yyyy/MM/dd"

            /**
             * 現在時刻をミリ秒単位で取得する。
             * */
            fun now(): Timestamp = Timestamp(System.currentTimeMillis())
        }
    }

    data class Source(val value: String)
}




