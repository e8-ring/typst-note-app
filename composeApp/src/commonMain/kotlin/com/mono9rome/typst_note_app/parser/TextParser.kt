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
                        // $ の中にある * は無視。
                        // 以下は *文章 $数式 * 数式$ 文章* のような場合への対処。
                        if (insideAsterisk) {
                            currentBoldExtracted.append(c)
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

        return Paragraph(elements)
    }

    context(_: Raise<Err>)
    private suspend fun parseInlineElements(text: String): List<InlineElement> =
        mathParser.parse(text, MathParser.MathType.Inline)
            .map { representation ->
                when (representation) {
                    is MathParser.Repr.Plain -> PlainText(representation.source)
                    is MathParser.Repr.Math -> {
                        val currentFontSize = fontSizeProvider.current
                        InlineMath(content = mathRenderer.toPng(representation.source, currentFontSize))
                    }
                }
            }
}