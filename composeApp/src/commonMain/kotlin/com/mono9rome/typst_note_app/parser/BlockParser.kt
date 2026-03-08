package com.mono9rome.typst_note_app.parser

import arrow.core.raise.Raise
import com.mono9rome.typst_note_app.model.*
import me.tatarka.inject.annotations.Inject

@Inject
class BlockParser(private val textParser: TextParser) {

    // グループ1: インデント, グループ2: マーカー, グループ3: テキスト（任意）
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
        // 上と同様

    enum class ListType { BULLET, NUMBERED }

    context(_: Raise<Err>)
    suspend fun parse(markdown: String): List<ContentBlock> {
        val lines = markdown.lines()
        val pos = intArrayOf(0)
        return parseBlocks(lines, pos, 0)
    }

    // 指定されたインデント(minIndent)以上のブロック群をパースする
    context(_: Raise<Err>)
    private suspend fun parseBlocks(lines: List<String>, pos: IntArray, minIndent: Int): List<ContentBlock> {
        val blocks = mutableListOf<ContentBlock>()

        while (pos[0] < lines.size) {
            val line = lines[pos[0]]
            if (line.isBlank()) {
                pos[0]++
                continue
            }

            val indent = countIndent(line)
            if (indent < minIndent) break // インデントが浅くなったらスコープ外なので終了

            val bulletMatch = bulletRegex.find(line)
            val numberedMatch = numberedRegex.find(line)

            if (bulletMatch != null && indent == bulletMatch.groups[1]?.value?.length) {
                blocks.add(parseList(lines, pos, indent, ListType.BULLET))
            } else if (numberedMatch != null && indent == numberedMatch.groups[1]?.value?.length) {
                blocks.add(parseList(lines, pos, indent, ListType.NUMBERED))
            } else {
                blocks.addAll(parsePrimitiveBlock(lines, pos, minIndent))
            }
        }
        return blocks
    }

    context(_: Raise<Err>)
    private suspend fun parseList(lines: List<String>, pos: IntArray, listIndent: Int, type: ListType): ContentBlock {
        val items = mutableListOf<ContentList.Item>()

        while (pos[0] < lines.size) {
            // リストアイテム間の空行を読み飛ばして次のアイテムを探す
            var peekPos = pos[0]
            while (peekPos < lines.size && lines[peekPos].isBlank()) peekPos++
            if (peekPos >= lines.size) {
                pos[0] = peekPos
                break
            }

            val line = lines[peekPos]
            val indent = countIndent(line)
            if (indent < listIndent) break // リスト終了

            val isBullet = bulletRegex.matches(line) && indent == listIndent
            val isNumbered = numberedRegex.matches(line) && indent == listIndent

            // リストの種類が変わったら別のリストとして扱う
            if (type == ListType.BULLET && !isBullet) break
            if (type == ListType.NUMBERED && !isNumbered) break

            pos[0] = peekPos
            items.add(parseListItem(lines, pos, type))
        }

        return if (type == ListType.BULLET) BulletList(items) else NumberedList(items)
    }

    context(_: Raise<Err>)
    private suspend fun parseListItem(lines: List<String>, pos: IntArray, type: ListType): ContentList.Item {
        val line = lines[pos[0]]
        val match = if (type == ListType.BULLET) bulletRegex.find(line)!! else numberedRegex.find(line)!!

        val textGroup = match.groups[3]
        // マーカーの直後のテキストが始まる位置を、このリスト項目の「基準インデント」とする
        val contentIndent = if (textGroup != null && textGroup.value.isNotEmpty()) {
            textGroup.range.first
        } else {
            match.groups[1]!!.value.length + match.groups[2]!!.value.length + 1
        }

        val firstLineText = textGroup?.value ?: ""
        pos[0]++

        val blocks = mutableListOf<ContentBlock>()

        // 1. マーカーと同じ行から始まるテキストを最初の段落としてパース
        if (firstLineText.isNotEmpty()) {
            val paragraphLines = mutableListOf(firstLineText)
            while (pos[0] < lines.size) {
                val nextLine = lines[pos[0]]
                if (nextLine.isBlank()) break // 空行で段落終了

                val indent = countIndent(nextLine)
                if (indent < contentIndent) break // インデント不足

                // 次の行が新しいリストの開始なら段落終了
                if ((bulletRegex.matches(nextLine) && indent == countIndent(nextLine)) ||
                    (numberedRegex.matches(nextLine) && indent == countIndent(nextLine))
                ) {
                    break
                }

                // 基準インデント分だけ先頭のスペースを削る
                paragraphLines.add(trimIndentFromLine(nextLine, contentIndent))
                pos[0]++
            }
            blocks.addAll(
                textParser.parse(paragraphLines.joinToString("\n"))
            )
        }

        // 2. 空行以降にある、同じリスト項目内の別段落やネストされたリストを再帰的に取得
        val childBlocks = parseBlocks(lines, pos, contentIndent)
        blocks.addAll(childBlocks)

        return ContentList.Item(blocks)
    }

    context(_: Raise<Err>)
    private suspend fun parsePrimitiveBlock(lines: List<String>, pos: IntArray, minIndent: Int): List<ContentBlock> {
        val textLines = mutableListOf<String>()
        while (pos[0] < lines.size) {
            val line = lines[pos[0]]
            if (line.isBlank()) {
                pos[0]++ // 空行は段落の区切り
                break
            }

            val indent = countIndent(line)
            if (indent < minIndent) break

            // リストマーカーが出現したら段落終了
            if ((bulletRegex.matches(line) && indent == countIndent(line)) ||
                (numberedRegex.matches(line) && indent == countIndent(line))
            ) {
                break
            }

            textLines.add(trimIndentFromLine(line, minIndent))
            pos[0]++
        }

        return textParser.parse(textLines.joinToString("\n"))
    }

    private fun countIndent(text: String): Int = text.takeWhile { it == ' ' }.length

    private fun trimIndentFromLine(line: String, indentToRemove: Int): String {
        return if (line.length >= indentToRemove && line.substring(0, indentToRemove).isBlank()) {
            line.substring(indentToRemove)
        } else {
            line.trimStart()
        }
    }
}