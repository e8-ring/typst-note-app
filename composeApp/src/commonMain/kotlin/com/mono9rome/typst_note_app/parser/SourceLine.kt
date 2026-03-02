package com.mono9rome.typst_note_app.parser

open class SourceLine(val value: String) {

    // グループ1: インデント, グループ2: マーカー, グループ3: テキスト（任意）
    private val bulletRegex = Regex("^(\\s*)([-*+])(?:\\s+(.*))?$")
        // ^ : 行の先頭
        // (\s*) : 空白文字の 0 回以上の繰り返し [グループ 1]
        // (-) : リストマーカー（ハイフン）[グループ 2]
        // (?:\s+(.*))? : マーカー後のスペースとテキスト
            // ?: : キャプチャしないグループを作る
            // \s+ : 1 個以上のスペース
            // (.*) : 0 個以上の任意の文字 [グループ 3]
            // ? : グループ全体が 0 or 1 回出現する
    private val numberedRegex = Regex("^(\\s*)(\\d+\\.)(?:\\s+(.*))?$")
        // 上と同様

    fun isBlank(): Boolean = value.isBlank()

    fun countIndent(): Int = value.takeWhile { it == ' ' }.length

    fun containsListMarker(): Boolean =
        bulletRegex.matches(value) || numberedRegex.matches(value)

    fun lineType(): LineType = when {
        bulletRegex.find(value) != null -> LineType.BulletListItem
        numberedRegex.find(value) != null -> LineType.NumberedListItem
        else -> LineType.ParagraphLine
    }

    fun removeIndent(currentIndentDepth: Int): SourceLine {
        check(value.length >= currentIndentDepth)
        return SourceLine(value.substring(currentIndentDepth))
    }

    fun listItemLineIndent(listType: ListType): Int {
        val match = when (listType) {
            ListType.BULLET  -> bulletRegex.find(value)
            ListType.NUMBERED -> numberedRegex.find(value)
        } ?: throw IllegalStateException("listItemLineIndent が正しく呼び出されていません")

        val textGroup = match.groups[3]

        // マーカーの直後のテキストが始まる位置
        return if (textGroup != null && textGroup.value.isNotEmpty()) {
            textGroup.range.first
        } else {
            match.groups[1]!!.value.length + match.groups[2]!!.value.length + 1
        }
    }
}