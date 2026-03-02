package com.mono9rome.typst_note_app.parser

import arrow.core.toNonEmptyListOrNull
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
        val lines = source.lines().toNonEmptyListOrNull() ?: return emptyList()
        val state = ParserState.new(lines)
        return with(state) {
            parseBlocks(0)
        }
    }



    // 指定されたインデント (minIndent) 以上のブロック群をパースする
    context(state: ParserState)
    private fun parseBlocks(
        lines: List<SourceLine>,
        minIndent: Int
    ): List<ContentBlock> {
        val blocks = mutableListOf<ContentBlock>()
        while (state.isNotReachedTheLastLine()) {
            // ループの各回の開始時点での現在の行を取得する
            val line = state.currentLine

            // この行が空行なら次の行に進む
            if (line.isBlank()) {
                // TODO 次の行へ
                continue
            }

            // インデントが浅くなったらこの行はスコープ外なので終了
            if (line.countIndent() < minIndent) break

            when (line.lineType()) {
                LineType.BulletListItem -> {
                    blocks.add(parseList(lines, line.countIndent(), ListType.BULLET))
                }
                LineType.NumberedListItem -> {
                    blocks.add(parseList(lines, line.countIndent(), ListType.NUMBERED))
                }
                LineType.ParagraphLine -> {
                    blocks.add(parseParagraph(lines, minIndent))
                }
            }
        }

        while (state.isNotReachedTheLastLine()) {

            val currentLine = state.currentLine

            // この行が空行なら次の行に進む
            if (currentLine.isBlank()) {
                stateManager.moveToNextLine()
                continue
            }

            val indent = countIndent(currentLine)

            // インデントが浅くなったらこの行はスコープ外なので終了
            if (indent < minIndent) break

            val bulletMatch = bulletRegex.find(currentLine)
            val numberedMatch = numberedRegex.find(currentLine)

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

    // 注意 : 現状、リスト項目間に空行を開けることは許されない。
    // リスト項目内の空行ならよい。
    context(state: ParserState)
    private fun parseList(
        lines: List<String>,
        listIndent: Int,
        type: ListType
    ): ContentList {
        val items = mutableListOf<ContentList.Item>()

        while (state.isNotReachedTheLastLine()) {
            // 処理（ここで行が進む）
            // 注意 : ループ最初はかならず制約条件をみたすから実行してよい
            items.add(parseListItem(lines, type))

            if (state.checkNextLineIsInCurrentListOrEOF(type, listIndent)) {
                state.moveToNextLine()
            } else break
        }

        return when (type) {
            ListType.BULLET -> BulletList(items)
            ListType.NUMBERED -> NumberedList(items)
        }
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

    context(state: ParserState)
    fun parseParagraph(
        minIndent: Int
    ): Paragraph {
        // 途中結果を保持するバッファ
        val textLines = mutableListOf<String>()

        while (state.isNotReachedTheLastLine()) {
            val line = state.currentLine

            // この行をバッファに追加
            // 注意 : この関数が呼び出される場合、最初の 1 行目は必ず段落の文章が存在するから、
            // ループ 1 周目は追加してもよい
            textLines.add(trimIndentFromLine(line, minIndent))

            // 次の行が同じ段落を構成しているなら、次へ進める。
            // そうでないなら、ループを抜ける。
            if (state.checkNextLineIsInCurrentParagraphOrEOF(minIndent)) {
                state.moveToNextLine()
            } else {
                break
            }
        }

        // 改行でつないで1 つの文字列に
        val paragraphAsString = textLines.joinToString("\n")

        return Paragraph(parseStyle(paragraphAsString))
    }

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