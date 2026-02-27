package com.mono9rome.typst_note_app.parser

import com.mono9rome.typst_note_app.model.InlineTextFragment
import com.mono9rome.typst_note_app.model.TextBlock

fun parseToTextBlocks(markdown: String): List<TextBlock> {
    // 正規表現パターン
    // Group 1: "$ " と " $" で囲まれたブロック数式
    // Group 2: "$" と "$" で囲まれたインライン数式
    // ※ 順番が重要。ブロック数式を先に評価させる。
    val regex = Regex("""\$ (.*?) \$|\$(.*?)\$""")

    // すべてのマッチ箇所
    val matchResults = regex.findAll(markdown)

    /* --- imperative --- */
    val textBlocks = mutableListOf<TextBlock>()
    val currentInlineText = mutableListOf<InlineTextFragment>()

    var lastIndex = 0 // 現在読み取っている文字列の位置

    for (match in matchResults) {
        // 1. マッチした箇所の「手前」までの文字列を取り出し、Plane（通常の文字列）として扱う
        val textBeforeMatch = markdown.substring(lastIndex, match.range.first).trimReturn()
        if (textBeforeMatch.isNotEmpty()) {
            currentInlineText.add(
                InlineTextFragment.Plane(textBeforeMatch)
            )
        }

        // 2. Group 1, 2 のどちらのパターンにマッチしたか判定
        val blockMathContent = match.groups[1]?.value
        val inlineMathContent = match.groups[2]?.value

        if (blockMathContent != null) {
            // --- ブロック数式にマッチした場合 ---

            // これまでに溜まっていたインライン要素があれば、1つの TextBlock.Inline として確定させる
            if (currentInlineText.isNotEmpty()) {
                textBlocks.add(
                    TextBlock.Inline(currentInlineText.toList())
                )
                currentInlineText.clear()
            }
            // ブロック数式を追加
            textBlocks.add(
                TextBlock.Math("$ $blockMathContent $")
            )

        } else if (inlineMathContent != null) {
            // --- インライン数式にマッチした場合 ---
            currentInlineText.add(
                InlineTextFragment.Math("$$inlineMathContent$")
            )
        }

        // 3. 次の検索開始位置を更新
        lastIndex = match.range.last + 1
    }

    // 最後のマッチ以降に残っている文字列を Plane として追加
    val remainingText = markdown.substring(lastIndex).trimReturn()
    if (remainingText.isNotEmpty()) {
        currentInlineText.add(
            InlineTextFragment.Plane(remainingText)
        )
    }

    // 残っているインライン要素があれば、最後の TextBlock.Inline として確定させる
    if (currentInlineText.isNotEmpty()) {
        textBlocks.add(
            TextBlock.Inline(currentInlineText.toList())
        )
    }

    return textBlocks
}

private fun String.trimReturn(): String = this.trim { it == '\n' || it == '\r' }