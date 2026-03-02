package com.mono9rome.typst_note_app.parser

import com.mono9rome.typst_note_app.model.BulletList
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.model.ContentList
import com.mono9rome.typst_note_app.model.NumberedList
import com.mono9rome.typst_note_app.model.Paragraph
import me.tatarka.inject.annotations.Inject

@Inject
class BlockParser(

) {
    // グループ1: インデント, グループ2: マーカー, グループ3: テキスト（任意）
    private val bulletRegex = Regex("^(\\s*)([-*+])(?:\\s+(.*))?$")
    private val numberedRegex = Regex("^(\\s*)(\\d+\\.)(?:\\s+(.*))?$")

    // 指定されたインデント (minIndent) 以上のブロック群をパースする
    // ※ pos は現在処理中の何行目を処理しているかを参照渡しで保持するための配列で、
    // 0 番目の要素しか用いない。Int が immutable なため。
    fun parseBlocks(
        lines: List<String>,
        pos: IntArray,
        minIndent: Int
    ): List<ContentBlock> {
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

            // 処理（pos[0] の値はここで増える）
            if (bulletMatch != null && indent == bulletMatch.groups[1]?.value?.length) {
                blocks.add(parseList(lines, pos, indent, SourceParser.ListType.BULLET))
            } else if (numberedMatch != null && indent == numberedMatch.groups[1]?.value?.length) {
                blocks.add(parseList(lines, pos, indent, SourceParser.ListType.NUMBERED))
            } else {
                blocks.add(parseParagraph(lines, pos, minIndent))
            }
        }

        return blocks
    }

    private fun parseList(
        lines: List<String>,
        pos: IntArray,
        listIndent: Int,
        type: SourceParser.ListType
    ): ContentList {
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
            if (type == SourceParser.ListType.BULLET && !isBullet) break
            if (type == SourceParser.ListType.NUMBERED && !isNumbered) break

            pos[0] = peekPos
            items.add(parseListItem(lines, pos, type))
        }

        return if (type == SourceParser.ListType.BULLET) BulletList(items) else NumberedList(items)
    }

    private fun parseListItem(
        lines: List<String>,
        pos: IntArray,
        type: SourceParser.ListType
    ): ContentList.Item {
        val line = lines[pos[0]]
        val match = if (type == SourceParser.ListType.BULLET) bulletRegex.find(line)!! else numberedRegex.find(line)!!

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
                    (numberedRegex.matches(nextLine) && indent == countIndent(nextLine))) {
                    break
                }

                // 基準インデント分だけ先頭のスペースを削る
                paragraphLines.add(trimIndentFromLine(nextLine, contentIndent))
                pos[0]++
            }
            blocks.add(Paragraph(parseStyle(paragraphLines.joinToString("\n"))))
        }

        // 2. 空行以降にある、同じリスト項目内の別段落やネストされたリストを再帰的に取得
        val childBlocks = parseBlocks(lines, pos, contentIndent)
        blocks.addAll(childBlocks)

        return ContentList.Item(blocks)
    }

    private fun countIndent(line: String): Int = line.takeWhile { it == ' ' }.length

    private fun trimIndentFromLine(line: String, indentToRemove: Int): String {
        return if (line.length >= indentToRemove && line.substring(0, indentToRemove).isBlank()) {
            line.substring(indentToRemove)
        } else {
            line.trimStart()
        }
    }
}