package com.mono9rome.typst_note_app.parser

import arrow.core.raise.Raise
import com.mono9rome.typst_note_app.FontSizeProvider
import com.mono9rome.typst_note_app.model.*
import com.mono9rome.typst_note_app.render.MathRenderer
import me.tatarka.inject.annotations.Inject

@Inject
class TextParser(
    private val mathParser: MathParser,
    private val mathRenderer: MathRenderer,
    private val fontSizeProvider: FontSizeProvider
) {

    context(_: Raise<Err>)
    suspend fun parse(text: String): List<ContentBlock> = parsePrimitiveBlock(text)

    context(_: Raise<Err>)
    private suspend fun parsePrimitiveBlock(text: String): List<ContentBlock> =
        mathParser.parse(text, MathParser.MathType.Block)
            .map { repr ->
                when (repr) {
                    is MathParser.Repr.Plain -> parseStyle(repr.source)
                    is MathParser.Repr.Math -> {
                        val currentFontSize = fontSizeProvider.current
                        BlockMath(mathRenderer.toPng(repr.source, currentFontSize))
                    }
                }
            }

    // 注意 : [text] にブロック数式は含まれない。
    context(_: Raise<Err>)
    private suspend fun parseStyle(text: String): Paragraph {
        val elements = mutableListOf<StyleElement>()

        // $ の中かどうかの状態変数
        var insideDollar = false
        // * の中かどうかの状態変数
        var insideAsterisk = false

        // * 外の文字をためておくバッファ
        val currentPlainExtracted = java.lang.StringBuilder()
        // 抽出中の * 内の文字をためておくバッファ
        val currentBoldExtracted = java.lang.StringBuilder()

        for (c in text) {
            when (c) {
                '$' -> {
                    // 中 / 外の状態を反転してから……
                    insideDollar = !insideDollar

                    if (insideAsterisk) {
                        // * で囲まれた内部のとき
                        currentBoldExtracted.append(c)
                    } else {
                        // * の外のとき
                        currentPlainExtracted.append(c)
                    }
                }

                '*' -> {
                    if (insideDollar) {
                        // $ の中にある * は太字に関与しない。
                        // しかし数式には関与するから、以下はそのため
                        if (insideAsterisk) {
                            currentBoldExtracted.append(c)
                        } else {
                            currentPlainExtracted.append(c)
                        }
                    } else {
                        // $ の外にある * は有効。
                        if (insideAsterisk) {
                            // 既に * 中だったら抽出終了
                            elements.add(
                                BoldNode(parseInlineElements(currentBoldExtracted.toString()))
                            )
                            // バッファをリセット
                            currentBoldExtracted.clear()
                            // 外に出る
                            insideAsterisk = false
                        } else {
                            // * の外にいた状態から * が始まるとき、プレーンテキスト抽出終了
                            elements.add(
                                PlainNode(parseInlineElements(currentPlainExtracted.toString()))
                            )
                            // バッファをリセット
                            currentPlainExtracted.clear()
                            // 新しく中に入る
                            insideAsterisk = true
                        }
                    }
                }

                else -> {
                    // その他の文字の場合
                    if (insideAsterisk) {
                        currentBoldExtracted.append(c)
                    } else {
                        currentPlainExtracted.append(c)
                    }
                }
            }
        }

        // ループ終了後にプレーンテキストのバッファが残っていたら確定する
        if (currentPlainExtracted.isNotEmpty()) {
            elements.add(
                PlainNode(parseInlineElements(currentPlainExtracted.toString()))
            )
        }

        return Paragraph(elements)
    }

    context(_: Raise<Err>)
    private suspend fun parseInlineElements(text: String): List<InlineElement> =
        mathParser.parse(text, MathParser.MathType.Inline)
            .flatMap { representation ->
                when (representation) {
                    is MathParser.Repr.Plain -> parseLink(representation.source)
                    is MathParser.Repr.Math -> {
                        val currentFontSize = fontSizeProvider.current
                        InlineMath(content = mathRenderer.toPng(representation.source, currentFontSize)).let(::listOf)
                    }
                }
            }

    private fun parseLink(text: String): List<InlineElement> {
        val elements = mutableListOf<InlineElement>()

        val regex = Regex("""(?<!\\)%(.*?)(?<!\\)%""")
        val matchResults = regex.findAll(text)

        var lastIndex = 0 // 現在読み取っている文字列の位置

        for (match in matchResults) {
            // 1. マッチした箇所の手前までの文字列を取り出し、Plane（通常の文字列）として扱う
            val textBeforeMatch = text.substring(lastIndex, match.range.first)
            if (textBeforeMatch.isNotEmpty()) {
                // $ 前のエスケープ文字 (\) を取り除く
                val unescapedText = textBeforeMatch.replace("\\%", "%").removeAllReturns()
                elements.add(PlainText(unescapedText))
            }

            // 2. リンクにマッチした場合
            val linkContent = match.groups[1]?.value
            if (linkContent != null) {
                // リンクを追加
                val link = LinkToNote(Note.Id(linkContent))
                elements.add(link)
            }

            // 3. 次の検索開始位置を更新
            lastIndex = match.range.last + 1
        }

        // 最後のマッチ以降に残っている文字列を Plain として追加
        val remainingText = text.substring(lastIndex)
        if (remainingText.isNotEmpty()) {
            val unescapedRemainingText = remainingText.replace("\\%", "%").removeAllReturns()
            elements.add(PlainText(unescapedRemainingText))
        }

        return elements
    }

    private fun String.removeAllReturns(): String = this.replace('\n', ' ')
}