package com.mono9rome.typst_note_app.parser

import arrow.core.NonEmptyList

class BlockParserState(private val lines: MutableList<SourceLine>) {

    // 今処理しているのが何行目かを管理する状態変数
    private var currentLineIndex: Int = 0

    // 今どのくらいの深さのブロックにいるか管理する状態変数 (半角スペースの個数)
    private var currentIndentDepth: Int = 0

    val currentLine: SourceLine = lines[currentLineIndex] // 範囲外にならないことはこのクラス内で保証する

    fun isNotExceededTheLastLine() = currentLineIndex < lines.size

    fun checkNextLineIsInCurrentParagraphOrEOF(currentParagraphIndentDepth: Int): Boolean {
        val existsNextLine = (currentLineIndex + 1 < lines.size)
        return if (existsNextLine) {
            // 次の行が空行でないこと（空行なら別段落）
            val isNextLineNotBlank = !(lines[currentLineIndex + 1].isBlank())
            // 次の行のインデントが今の行より下がっていないこと
            val isNextLineIndentDepthMoreShallow = (lines[currentLineIndex + 1].countIndent() >= currentParagraphIndentDepth)
            // 次の行が list item でないこと
            val isNextLineNotListItem = (lines[currentLineIndex + 1].containsListMarker().not())

            isNextLineNotBlank && isNextLineIndentDepthMoreShallow && isNextLineNotListItem
        } else {
            // 次の行が存在しない場合は true
            true
        }
    }
    // 補足 : 現状、段落の次の行としてあり得るのは以下のみ :
    // - 同じ段落の文章
    // - 空行（別段落への移行）
    // - インデント数が等しい '-'（bullet list の最初の項目）
    // - インデント数が等しい '+'（numbered list の最初の項目）

    fun checkNextLineIsInCurrentListOrEOF(
        currentListType: ListType,
        currentIndentDepth: Int
    ): Boolean = (currentLineIndex + 1 == lines.size) // 次の行が存在しない場合
            || ((currentLineIndex + 1 < lines.size) // 次の行が存在すること
            && (!(lines[currentLineIndex + 1].isBlank())) // 次の行が空行でないこと
            && (lines[currentLineIndex + 1].countIndent() == currentIndentDepth) // (*1)
            && (((currentListType == ListType.BULLET) && (lines[currentLineIndex + 1].lineType() != LineType.NumberedListItem)) // (*2)
            || ((currentListType == ListType.NUMBERED) && (lines[currentLineIndex + 1].lineType() != LineType.BulletListItem)))) // (*3)
    // 補足
    // (*1) 現在の行のインデント数より次の行のインデント数が大きい => 内部の別リストになる
    //      小さい => 現在のリストは終了して外へ行く
    // (*2) 現在の行が bullet list なのに次の行が numbered list にならない
    // (*3) 現在の行が numbered list なのに次の行が bullet list にならない

    // 状態更新
    fun moveToNextLine() {
        currentLineIndex += 1
    }

    // 状態更新
    fun skipBlankLines() {
        // 先の行を見に行くための変数を定義
        var peekPosition = currentLineIndex
        // 空でない行がでてくるまで進める
        while (peekPosition < lines.size && lines[peekPosition].isBlank()) peekPosition++
        // ※ ファイル最後まで空行だった場合 peekPosition = lines.size となった時点で終了する

        // 現在位置を最初の空でない行に設定
        currentLineIndex = peekPosition
    }

    // 状態更新
    fun removeListMarkerFromCurrentLine(currentIndentDepth: Int) {
        lines[currentLineIndex] = lines[currentLineIndex].removeIndent(currentIndentDepth)
    }

    companion object {
        fun new(lines: NonEmptyList<String>): BlockParserState =
            BlockParserState(lines.map(::SourceLine).toMutableList())
    }
}