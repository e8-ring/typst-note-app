package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.em
import com.mono9rome.typst_note_app.model.InlineElement
import com.mono9rome.typst_note_app.model.InlineMath
import com.mono9rome.typst_note_app.model.PlainText
import com.mono9rome.typst_note_app.ui.viewer.renderer.InlineElementTemplate
import org.jetbrains.skia.Image.Companion.makeFromEncoded

actual fun List<InlineElement>.toTemplate(fontSizeSp: Float): InlineElementTemplate {
    val annotatedString = buildAnnotatedString {
        this@toTemplate.forEach { element ->
            when (element) {
                is PlainText -> {
                    append(element.content)
                }
                is InlineMath -> {
                    appendInlineContent(
                        id = element.id.value,
                        alternateText = element.id.value // TODO 考える
                    )
                }
            }
        }
    }

    // インライン数式の id と表示用 Composable のペア
    val inlineContentMap = this.filterIsInstance<InlineMath>()
        .associate { element ->
            val bitmap = makeFromEncoded(element.content.content).toComposeImageBitmap()

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

    return InlineElementTemplate(
        annotatedString = annotatedString,
        inlineContentMap = inlineContentMap
    )
}