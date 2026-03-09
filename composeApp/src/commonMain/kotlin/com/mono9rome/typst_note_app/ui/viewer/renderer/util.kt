package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.em
import com.mono9rome.typst_note_app.core.makeBitmapFromEncoded
import com.mono9rome.typst_note_app.model.*

fun List<InlineElement>.makeAnnotatedString(onClickLink: (Note.Id) -> Unit): AnnotatedString =
    buildAnnotatedString {
        this@makeAnnotatedString.forEach { element ->
            when (element) {
                is PlainText -> {
                    append(element.content)
                }

                is InlineMath -> {
                    appendInlineContent(id = element.id.value)
                }

                is LinkToNote -> {
                    withLink(
                        link = LinkAnnotation.Clickable(
                            tag = element.noteId.value
                        ) {
                            onClickLink(element.noteId)
                        }
                    ) {
                        append(element.noteId.value)
                    }
                }
            }
        }
    }

fun List<InlineElement>.makeInlineContentMap(fontSizeSp: Float): Map<UniqueId, InlineTextContent> = this.filterIsInstance<InlineMath>()
    .associate { element ->
        val bitmap = makeBitmapFromEncoded(element.content.content) ?: return emptyMap()

        // px を em に変換
        // 注意 : 1 em は現在指定されている fontSize (sp) のサイズと等しい。
        // 注意 : sp ≒ px.
        val heightEm = bitmap.height.toFloat() / fontSizeSp
        val widthEm = bitmap.width.toFloat() / fontSizeSp

        val inlineTextContent = InlineTextContent(
            Placeholder(
                width = widthEm.em,
                height = heightEm.em,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Typst Math Equation",
                    contentScale = ContentScale.None
                )
            }
        }

        element.id to inlineTextContent
    }