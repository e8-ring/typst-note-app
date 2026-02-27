package com.mono9rome.typst_note_app.parser

import com.mono9rome.typst_note_app.parser.SourceParser.ListType

class ListParser {
    /* --- models --- */
    sealed interface PreContentBlock
    data class Paragraphs(val value: String) : PreContentBlock
    sealed interface PreContentList : PreContentBlock {
        val items: List<Item>

        data class Item(val blocks: List<PreContentBlock>)
    }
    data class PreBulletList(override val items: List<PreContentList.Item>) : PreContentList
    data class PreNumberedList(override val items: List<PreContentList.Item>) : PreContentList

    /* --- methods --- */

    // グループ 1: インデント, グループ 2: マーカー, グループ 3: テキスト（任意）
    private val bulletRegex = Regex("^(\\s*)(-)(?:\\s+(.*))?$")
        // ^ : 行の先頭
        // (\s*) : 空白文字の 0 回以上の繰り返し [グループ 1]
        // (-) : リストマーカー（ハイフン）[グループ 2]
        // (?:\s+(.*))? : マーカー後のスペースとテキスト
            // ?: : キャプチャしないグループを作る
            // \s+ : 1 個以上のスペース
            // (.*) : 0 個以上の任意の文字 [グループ 3]
            // ? : グループ全体が 0 or 1 回出現する
    private val numberedRegex = Regex("^(\\s*)(\\+)(?:\\s+(.*))?$")

    fun parseBlocks(
        sourceByLines: List<String>,
        pos: IntArray,
        minIndent: Int
    ): List<PreContentBlock> {
        val results = mutableListOf<PreContentBlock>()

        // pos[0] は現在何番目の行か
        while (pos[0] < sourceByLines.size) {
            val line = sourceByLines[pos[0]]

            // 空行の場合スキップ
            if (line.isBlank()) {
                pos[0]++
                continue
            }

            val indentNumber = countIndent(line)
            if (indentNumber < minIndent) break // インデントが浅くなったらスコープ外なので終了

            // この行が bullet or numbered list の先頭かどうか
            val bulletMatch = bulletRegex.find(line)
            val numberedMatch = numberedRegex.find(line)

            if (bulletMatch != null && indentNumber == bulletMatch.groups[1]?.value?.length) {
                // bullet list の先頭の場合
                results.add(parseList(sourceByLines, pos, indentNumber, ListType.BULLET))
            } else if (numberedMatch != null && indentNumber == numberedMatch.groups[1]?.value?.length) {
                // numbered list の先頭の場合
                results.add(parseList(sourceByLines, pos, indentNumber, ListType.NUMBERED))
            } else {
                // それ以外の場合、この行は
                results.add(parseParagraph(sourceByLines, pos, minIndent))
            }
        }

        return results
    }

    private fun countIndent(line: String): Int = line.takeWhile { it == ' ' }.length
}