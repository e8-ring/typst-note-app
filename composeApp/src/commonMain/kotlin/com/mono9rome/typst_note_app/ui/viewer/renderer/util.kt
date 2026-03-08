package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.text.AnnotatedString
import com.mono9rome.typst_note_app.model.InlineElement
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.model.UniqueId

expect fun List<InlineElement>.makeAnnotatedString(onClickLink: (Note.Id) -> Unit): AnnotatedString

expect fun List<InlineElement>.makeInlineContentMap(fontSizeSp: Float): Map<UniqueId, InlineTextContent>