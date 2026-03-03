package com.mono9rome.typst_note_app.ui.renderer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        modifier = modifier.fillMaxWidth()
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
                        fontSizeSp = textSizeSp
                    )
                }
            }
            Spacer(Modifier.height((textSizeSp * 0.5).dp))
        }
    }
}