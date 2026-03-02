package com.mono9rome.typst_note_app.parser

data class SourceLine(private val value: String) {

    // グループ1: インデント, グループ2: マーカー, グループ3: テキスト（任意）
    private val bulletRegex = Regex("^(\\s*)([-*+])(?:\\s+(.*))?$")
    private val numberedRegex = Regex("^(\\s*)(\\d+\\.)(?:\\s+(.*))?$")

    fun isBlank(): Boolean = value.isBlank()

    fun countIndent(): Int = value.takeWhile { it == ' ' }.length

    fun containsListMarker(): Boolean =
        bulletRegex.matches(value) || numberedRegex.matches(value)

    fun lineType(): LineType = when {
        bulletRegex.find(value) != null -> LineType.BulletListItem
        numberedRegex.find(value) != null -> LineType.NumberedListItem
        else -> LineType.ParagraphLine
    }

    fun isFirstLineOfBulletList(): MatchResult? = bulletRegex.find(value)

    fun isFirstLineOfNumbered(): MatchResult? = numberedRegex.find(value)

    companion object {
        fun new(value: String): SourceLine {
            return SourceLine(value)
        }
    }
}