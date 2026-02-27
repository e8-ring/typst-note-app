package com.mono9rome.typst_note_app.parser

import com.mono9rome.typst_note_app.model.BoldNode
import com.mono9rome.typst_note_app.model.BulletList
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.model.ContentList
import com.mono9rome.typst_note_app.model.NumberedList
import com.mono9rome.typst_note_app.model.Paragraph
import com.mono9rome.typst_note_app.model.RomanNode
import com.mono9rome.typst_note_app.model.StyleElement

class SourceParser {

    // グループ1: インデント, グループ2: マーカー, グループ3: テキスト（任意）
    private val bulletRegex = Regex("^(\\s*)([-*+])(?:\\s+(.*))?$")
    private val numberedRegex = Regex("^(\\s*)(\\d+\\.)(?:\\s+(.*))?$")

    private val boldRegex = Regex()

    enum class ListType { BULLET, NUMBERED }

    // 今何行目を読んでいるか管理する状態変数
    private var currentLineNumber: Int = 0

    private val stateManager = StateManager()

    fun parse(source: String): List<ContentBlock> {
        val lines = source.lines()

        return parseBlocks(lines, 0).also {
            stateManager.initState()
        }
    }



    // 指定されたインデント (minIndent) 以上のブロック群をパースする
    private fun parseBlocks(
        lines: List<String>,
        minIndent: Int
    ): List<ContentBlock> {
        val blocks = mutableListOf<ContentBlock>()

        while (currentLineNumber < lines.size) {

            val line = lines[currentLineNumber]

            // この行が空行なら次の行に進む
            if (line.isBlank()) {
                stateManager.moveToNextLine()
                continue
            }

            val indent = countIndent(line)

            // インデントが浅くなったらこの行はスコープ外なので終了
            if (indent < minIndent) break

            val bulletMatch = bulletRegex.find(line)
            val numberedMatch = numberedRegex.find(line)

            // 処理（ここで行が進む）
            if (bulletMatch != null && indent == bulletMatch.groups[1]?.value?.length) {
                blocks.add(parseList(lines, indent, ListType.BULLET))
            } else if (numberedMatch != null && indent == numberedMatch.groups[1]?.value?.length) {
                blocks.add(parseList(lines, indent, ListType.NUMBERED))
            } else {
                blocks.add(parseParagraph(lines, pos, minIndent))
            }
        }
        return blocks
    }

    private fun parseList(
        lines: List<String>,
        listIndent: Int,
        type: ListType
    ): ContentList {
        val items = mutableListOf<ContentList.Item>()

        while (currentLineNumber < lines.size) {
            // リストアイテム間の空行を読み飛ばして次のアイテムを探す
            var peekPos = currentLineNumber
            while (peekPos < lines.size && lines[peekPos].isBlank()) peekPos++
            if (peekPos >= lines.size) {
                currentLineNumber = peekPos
                break
            }

            val line = lines[peekPos]
            val indent = countIndent(line)

            // インデントが浅くなったらこの行はリストの外だから終了
            if (indent < listIndent) break

            // リストの種類が変わっていたら別のリストとして扱うため終了
            val isBullet = bulletRegex.matches(line) && indent == listIndent
            val isNumbered = numberedRegex.matches(line) && indent == listIndent
            if (type == ListType.BULLET && !isBullet) break
            if (type == ListType.NUMBERED && !isNumbered) break

            currentLineNumber = peekPos

            // 処理（ここで行が進む）
            items.add(parseListItem(lines, type))
        }

        return if (type == ListType.BULLET) BulletList(items) else NumberedList(items)
    }

    private fun parseListItem(
        lines: List<String>,
        type: ListType
    ): ContentList.Item {
        val line = lines[currentLineNumber]
        val match = if (type == ListType.BULLET) bulletRegex.find(line)!! else numberedRegex.find(line)!!

        val textGroup = match.groups[3]

        // マーカーの直後のテキストが始まる位置を、このリスト項目の「基準インデント」とする
        val contentIndent = if (textGroup != null && textGroup.value.isNotEmpty()) {
            textGroup.range.first
        } else {
            match.groups[1]!!.value.length + match.groups[2]!!.value.length + 1
        }

        // リストの最初の行
        val firstLineText = textGroup?.value ?: ""

        // 次の行へ
        stateManager.moveToNextLine()

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

    private inner class StateManager {
        fun initState() {
            currentLineNumber = 0
        }

        fun moveToNextLine() {
            currentLineNumber++
        }
    }

    private inner class ParagraphParser {
        fun run(
            lines: List<String>,
            pos: IntArray,
            minIndent: Int
        ): Paragraph {
            // 途中結果を保持するバッファ
            val textLines = mutableListOf<String>()

            while (pos[0] < lines.size) {
                val line = lines[pos[0]]

                // この行が空行なら段落はこの行で終了
                if (line.isBlank()) {
                    // 次へ進めてから抜ける
                    pos[0]++
                    break
                }

                // インデントが下がっていたら段落は 1 行前で終了
                val indent = countIndent(line)
                if (indent < minIndent) break

                // リストマーカーが出現したら段落は 1 行前で終了
                if (line.containsListMarker()) {
                    break
                }

                // この行をバッファに追加
                textLines.add(trimIndentFromLine(line, minIndent))

                // 次の行へ
                pos[0]++
            }

            // 改行でつないで1 つの文字列に
            val paragraphAsString = textLines.joinToString("\n")

            return Paragraph(parseStyle(paragraphAsString))
        }

        private fun String.containsListMarker(): Boolean =
            bulletRegex.matches(this) || numberedRegex.matches(this)

        private fun parseStyle(text: String): List<StyleElement> {
            val elements = mutableListOf<StyleElement>()
            var lastIndex = 0

            for (match in boldRegex.findAll(text)) {
                val startIndex = match.range.first
                // マッチした部分より前のテキストを TextNode として追加
                if (startIndex > lastIndex) {
                    elements.add(RomanNode(text.substring(lastIndex, startIndex)))
                }
                // 囲まれていた中身を ItalicNode として追加 (groupValues[2] が中身)
                elements.add(BoldNode(match.groupValues[2]))
                lastIndex = match.range.last + 1
            }

            // 最後に残ったテキストを追加
            if (lastIndex < text.length) {
                elements.add(RomanNode(text.substring(lastIndex)))
            }

            return elements
        }
    }
    
}