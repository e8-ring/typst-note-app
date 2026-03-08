package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.mono9rome.typst_note_app.model.*

actual fun List<InlineElement>.makeAnnotatedString(onClickLink: (Note.Id) -> Unit): AnnotatedString =
    buildAnnotatedString {
        this@makeAnnotatedString.forEach { element ->
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

                is LinkToNote -> {
                    append(element.noteId.value)
                }
            }
        }
    }

actual fun List<InlineElement>.makeInlineContentMap(fontSizeSp: Float): Map<UniqueId, InlineTextContent> = mapOf()