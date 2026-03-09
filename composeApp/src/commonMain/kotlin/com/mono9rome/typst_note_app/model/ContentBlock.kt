package com.mono9rome.typst_note_app.model

import com.mono9rome.typst_note_app.core.renderer.MathRepr
import java.util.UUID


// 全体の共通インターフェース
sealed interface ContentBlock

/*
* ContentBlock
* -- Paragraph
* -- BlockMath
*    -- ParsedBlockMath
*    -- RenderedBlockMath
* -- ContentList
*    -- BulletList
*    -- NumberedList
* */

/* --- リストレベル --- */

// リストの共通インターフェース
sealed interface ContentList : ContentBlock {
    val items: List<Item>

    // リスト項目
    data class Item(val blocks: List<ContentBlock>)
}

// 箇条書きリスト
data class BulletList(override val items: List<ContentList.Item>) : ContentList

// 番号付きリスト
data class NumberedList(override val items: List<ContentList.Item>) : ContentList

/* --- 段落レベル --- */

// ブロック数式
data class BlockMath(val content: MathRepr) : ContentBlock

// 段落
data class Paragraph(val content: List<StyleElement>) : ContentBlock

sealed interface StyleElement {
    val content: List<InlineElement>
}
data class PlainNode(override val content: List<InlineElement>) : StyleElement
data class BoldNode(override val content: List<InlineElement>) : StyleElement

/* --- インライン要素 --- */

sealed interface InlineElement
data class PlainText(val content: String) : InlineElement
data class InlineMath(
    val id: UniqueId = UUID.randomUUID().toString().let(::UniqueId),
    val content: MathRepr,
) : InlineElement
data class LinkToNote(val noteId: Note.Id) : InlineElement

@JvmInline value class UniqueId(val value: String)