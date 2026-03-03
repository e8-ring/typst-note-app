package com.mono9rome.typst_note_app.ui.renderer

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.text.AnnotatedString
import com.mono9rome.typst_note_app.model.InlineElement
import com.mono9rome.typst_note_app.model.UniqueId

data class InlineElementTemplate(
    val annotatedString: AnnotatedString,
    val inlineContentMap: Map<UniqueId, InlineTextContent>
)

expect fun List<InlineElement>.toTemplate(fontSizeSp: Float): InlineElementTemplate