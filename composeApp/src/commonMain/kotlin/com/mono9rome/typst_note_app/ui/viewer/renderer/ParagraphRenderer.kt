package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.model.BoldNode
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.model.Paragraph
import com.mono9rome.typst_note_app.model.PlainNode

@Composable
fun ParagraphRenderer(
    currentNoteId: Note.Id,
    paragraph: Paragraph,
    fontSizeSp: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (!paragraph.content.isEmpty()) {
            // クリックされて展開状態にある note id を保持する Set
            var expandedItems by remember(currentNoteId) { mutableStateOf(setOf<Note.Id>()) }

            // スタイルをつける
            val annotatedString =
                buildAnnotatedString {
                    paragraph.content.forEach { element ->
                        val originalAnnotatedString = element.content.makeAnnotatedString { noteId ->
                            expandedItems = if (expandedItems.contains(noteId)) {
                                expandedItems - noteId
                            } else {
                                expandedItems + noteId
                            }
                        }
                        when (element) {
                            is PlainNode -> {
                                append(originalAnnotatedString)
                            }

                            is BoldNode -> {
                                withStyle(
                                    style = SpanStyle(fontWeight = FontWeight.Bold)
                                ) {
                                    append(originalAnnotatedString)
                                }
                            }
                        }
                    }
                }

            // 子要素の inlineContentMap をすべて結合
            val inlineContentMap = paragraph.content
                .map { element ->
                    element.content.makeInlineContentMap(fontSizeSp).mapKeys { (key, _) -> key.value }
                }
                .reduce { acc, map -> acc + map }

            // 描画
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = annotatedString,
                    fontSize = fontSizeSp.sp,
                    lineHeight = (fontSizeSp * 1.5).sp,
                    inlineContent = inlineContentMap
                )
                SpacerBetweenBlocks(fontSizeSp)
                expandedItems.forEach { noteId ->
                    if (noteId != currentNoteId) {
                        NestedContentRenderer(
                            rootNoteId = currentNoteId,
                            noteId = noteId,
                            fontSizeSp = fontSizeSp,
                        )
                        SpacerBetweenBlocks(fontSizeSp)
                    } else {
                        Text("Recurring.")
                    }
                }
            }
        }
    }
}