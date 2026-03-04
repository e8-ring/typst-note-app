package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.buildAnnotatedString
import com.mono9rome.typst_note_app.model.InlineElement
import com.mono9rome.typst_note_app.model.InlineMath
import com.mono9rome.typst_note_app.model.PlainText
import com.mono9rome.typst_note_app.model.UniqueId
import com.mono9rome.typst_note_app.ui.viewer.renderer.InlineElementTemplate

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

    val inlineContentMap = mapOf<UniqueId, InlineTextContent>()

    return InlineElementTemplate(
        annotatedString = annotatedString,
        inlineContentMap = inlineContentMap
    )
}