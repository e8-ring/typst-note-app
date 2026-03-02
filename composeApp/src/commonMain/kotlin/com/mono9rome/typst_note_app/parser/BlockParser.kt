package com.mono9rome.typst_note_app.parser

import arrow.core.raise.Raise
import arrow.core.raise.context.ensure
import arrow.core.toNonEmptyListOrNull
import com.mono9rome.typst_note_app.model.*
import me.tatarka.inject.annotations.Inject

@Inject
class BlockParser(private val textParser: TextParser) {

    context(_: Raise<Err>)
    suspend fun parse(source: String): List<ContentBlock> {
        val lines = source.lines().toNonEmptyListOrNull() ?: return emptyList()
        val state = BlockParserState.new(lines)
        return with(state) {
            parseBlocks(0)
        }
    }

    // 指定されたインデント (minIndent) 以上のブロック群をパースする
    // 注意 : インデントが深くなるのは新しいリストに入ったときしかない。
    context(state: BlockParserState, _: Raise<Err>)
    private suspend fun parseBlocks(
        currentIndent: Int
    ): List<ContentBlock> {
        val blocks = mutableListOf<ContentBlock>()

        // 空でない行が出てくるまで飛ばす（主に最初の段階を想定）
        state.skipBlankLines()

        while (state.isNotExceededTheLastLine()) {
            // ループの各回の開始時点での現在の行を取得する
            val line = state.currentLine

            // この行が空行なら次の行に進む
            if (line.isBlank()) {
                println("line is blank!")
                state.moveToNextLine()
                continue
            }

            // インデントが浅くなったらこの行はスコープ外なので終了
            if (line.countIndent() < currentIndent) break

            // ブロックレベルでインデントが深くなることはない
            // インデントが深くなるのは List の内部のみ
            ensure(line.countIndent() <= currentIndent) { Err("インデントエラー") }

            // 通常時の場合の処理
            // 注意 : 呼び出し元をみると、ループ 1 回目は制約条件がみたされている。
            when (line.lineType()) {
                LineType.BulletListItem -> {
                    blocks.add(parseList(line.countIndent(), ListType.BULLET))
                }
                LineType.NumberedListItem -> {
                    blocks.add(parseList(line.countIndent(), ListType.NUMBERED))
                }
                LineType.ParagraphLine -> {
                    blocks.addAll(parsePrimitiveBlock(currentIndent))
                }
            }
        }

        return blocks
    }

    // 注意 : 現状、リスト項目間に空行を開けることは許されない。
    // リスト項目内の空行ならよい。
    context(state: BlockParserState, _: Raise<Err>)
    private suspend fun parseList(
        currentIndent: Int,
        listType: ListType
    ): ContentList {
        val items = mutableListOf<ContentList.Item>()

        while (state.isNotExceededTheLastLine()) {
            // 処理（ここで行が進む）
            // 注意 : ループ最初はかならず制約条件をみたすから実行してよい
            items.add(parseListItem(listType))

            if (state.checkNextLineIsInCurrentListOrEOF(listType, currentIndent)) {
                state.moveToNextLine()
            } else break
        }

        return when (listType) {
            ListType.BULLET -> BulletList(items)
            ListType.NUMBERED -> NumberedList(items)
        }
    }

    // これは必ずリスト項目の 1 行目（リストマーカーが付いている行）で実行される
    context(state: BlockParserState, _: Raise<Err>)
    private suspend fun parseListItem(listType: ListType): ContentList.Item {
        // 注意 : リストマーカー付きの行からインデントが深くなる
        val baseIndent = state.currentLine.listItemLineIndent(listType)

        // 現在の行からリストマーカーを削除
        state.removeListMarkerFromCurrentLine(baseIndent)

        // この行のインデントを基準にして、再帰的にブロックをパース
        return ContentList.Item(parseBlocks(baseIndent))
    }

    // 注意 : 返り値のリストは ContentBlock.Paragraph と ContentBlock.BlockMath のみからなる
    context(state: BlockParserState, _: Raise<Err>)
    private suspend fun parsePrimitiveBlock(
        currentIndent: Int
    ): List<ContentBlock> {
        // 途中結果を保持するバッファ
        val textLines = mutableListOf<SourceLine>()

        while (state.isNotExceededTheLastLine()) {
            val line = state.currentLine

            // この行をバッファに追加
            // 注意 : この関数が呼び出される場合、最初の 1 行目は必ず段落の文章が存在するから、
            // ループ 1 周目は追加してもよい
            textLines.add(line.removeIndent(currentIndent))

            // 次の行が同じ段落を構成しているなら、次へ進める。
            // そうでないなら、ループを抜ける。
            if (state.checkNextLineIsInCurrentParagraphOrEOF(currentIndent)) {
                state.moveToNextLine()
            } else break
        }

        // 改行でつないで1 つの文字列に
        val paragraphAsString = textLines.joinToString("\n", transform = SourceLine::value)

//        return textParser.parse(paragraphAsString)
        return listOf(Paragraph(listOf(PlainNode(listOf(PlainText(paragraphAsString))))))
    }
}