package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.model.BoldNode
import com.mono9rome.typst_note_app.model.Paragraph
import com.mono9rome.typst_note_app.model.PlainNode

@Composable
fun ParagraphRenderer(
    paragraph: Paragraph,
    fontSizeSp: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (!paragraph.content.isEmpty()) {
            // スタイルをつける
            val annotatedString = remember(paragraph) {
                buildAnnotatedString {
                    paragraph.content.forEach { line ->
                        val originalAnnotatedString = line.content.toTemplate(fontSizeSp).annotatedString
                        when (line) {
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
            }

            // 子要素の inlineContentMap をすべて結合
            val inlineContentMap = remember(paragraph) {
                paragraph.content
                    .map { element ->
                        element.content.toTemplate(fontSizeSp).inlineContentMap.mapKeys { (key, _) -> key.value }
                    }
                    .reduce { acc, map -> acc + map }
            }

            Text(
                text = annotatedString,
                fontSize = fontSizeSp.sp,
                lineHeight = (fontSizeSp * 1.5).sp,
                inlineContent = inlineContentMap
            )
        }
    }
}