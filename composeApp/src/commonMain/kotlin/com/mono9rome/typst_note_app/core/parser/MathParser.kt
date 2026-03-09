package com.mono9rome.typst_note_app.core.parser

import me.tatarka.inject.annotations.Inject

@Inject
class MathParser {

    sealed interface Repr {
        val source: String

        data class Plain(override val source: String) : Repr
        data class Math(override val source: String) : Repr
    }

    enum class MathType { Inline, Block }

    fun parse(text: String, mathType: MathType): List<Repr> {
        val reprs = mutableListOf<Repr>()

        // ※ 「直前に \ がないこと」を意味する否定あと読み (?<!\\) をつけることでエスケープ処理に対応
        // ※ ブロック数式の場合の先頭の ^ に注意！現状、これをつけないと「$inline$ hoge $inline$」という状況で
        // 中央の $ hoge $ が検知されてしまう。
        val regex = when (mathType) {
            MathType.Inline -> Regex("""(?<!\\)\$(.*?)(?<!\\)\$""")
            MathType.Block -> Regex("""^(?<!\\)\$ (.*?) (?<!\\)\$""")
        }
        val matchResults = regex.findAll(text)

        var lastIndex = 0 // 現在読み取っている文字列の位置

        for (match in matchResults) {
            // 1. マッチした箇所の手前までの文字列を取り出し、Plane（通常の文字列）として扱う
            val textBeforeMatch = text.substring(lastIndex, match.range.first).trimReturn()
            if (textBeforeMatch.isNotEmpty()) {
                // $ 前のエスケープ文字 (\) を取り除く
                val unescapedText = textBeforeMatch.replace("\\$", "$")
                reprs.add(Repr.Plain(unescapedText))
            }

            // 2. 数式にマッチした場合
            val mathContent = match.groups[1]?.value
            if (mathContent != null) {
                // 数式を追加
                val repr = Repr.Math(
                    source = when (mathType) {
                        MathType.Inline -> "$$mathContent$"
                        MathType.Block -> "$ $mathContent $"
                    }
                )
                reprs.add(repr)
            }

            // 3. 次の検索開始位置を更新
            lastIndex = match.range.last + 1
        }

        // 最後のマッチ以降に残っている文字列を Plain として追加
        val remainingText = text.substring(lastIndex).trimReturn()
        if (remainingText.isNotEmpty()) {
            val unescapedRemainingText = remainingText.replace("\\$", "$")
            reprs.add(Repr.Plain(unescapedRemainingText))
        }

        return reprs
    }

    private fun String.trimReturn(): String = this.trim { it == '\n' || it == '\r' }
}