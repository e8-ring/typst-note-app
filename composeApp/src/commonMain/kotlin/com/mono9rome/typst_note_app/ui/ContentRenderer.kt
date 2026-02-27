package com.mono9rome.typst_note_app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mono9rome.typst_note_app.model.BlockMath
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.model.ContentList
import com.mono9rome.typst_note_app.model.Paragraph

@Composable
fun ContentRenderer(
    contentBlocks: List<ContentBlock>,
    textSizeSp: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        contentBlocks.forEach { contentBlock ->
            when (contentBlock) {
                is Paragraph -> {
                    ParagraphRenderer(
                        paragraph = contentBlock,
                        fontSizeSp = textSizeSp,
                    )
                }
                is BlockMath -> {
                    BlockMathRenderer(
                        blockMath = contentBlock
                    )
                }
                is ContentList -> {
                    ListRenderer(
                        contentList = contentBlock,
                        textSizeSp = textSizeSp
                    )
                }
            }
        }
    }
}