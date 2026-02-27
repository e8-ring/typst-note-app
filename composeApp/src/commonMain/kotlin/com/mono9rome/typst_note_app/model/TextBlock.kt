package com.mono9rome.typst_note_app.model

// 1 行の文章の構成要素
sealed interface InlineTextFragment {
    val content: String

    // 数式を含まない文字列
    data class Plane(override val content: String) : InlineTextFragment

    // インライン数式のコード
    data class Math(override val content: String) : InlineTextFragment
}

typealias InlineText = List<InlineTextFragment>

// 1 行の 1 行の文章の種類
sealed interface TextBlock {
    // インライン数式を含む通常の文章
    data class Inline(val text: InlineText) : TextBlock

    // ブロック数式（のコード）
    data class Math(val content: String) : TextBlock
}

/* --- render 済みデータ --- */

sealed interface RenderedInlineTextFragment {
    // 数式を含まない文字列
    data class Plane(val content: String) : RenderedInlineTextFragment

    // インライン数式
    data class Math(
        val source: String,
        val content: ByteArray
    ) : RenderedInlineTextFragment
}

typealias RenderedInlineText = List<RenderedInlineTextFragment>

sealed interface RenderedTextBlock {
    // インライン数式を含む通常の文章
    data class Inline(val text: RenderedInlineText) : RenderedTextBlock

    // ブロック数式
    data class Math(
        val source: String,
        val content: ByteArray
    ) : RenderedTextBlock
}
