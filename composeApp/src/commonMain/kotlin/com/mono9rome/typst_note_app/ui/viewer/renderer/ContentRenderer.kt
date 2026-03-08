package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mono9rome.typst_note_app.model.BlockMath
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.model.ContentList
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.model.Paragraph

@Composable
fun ContentRenderer(
    currentNoteId: Note.Id,
    contentBlocks: List<ContentBlock>,
    fontSizeSp: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        contentBlocks.forEachIndexed { index, contentBlock ->
            when (contentBlock) {
                is Paragraph -> {
                    ParagraphRenderer(
                        currentNoteId = currentNoteId,
                        paragraph = contentBlock,
                        fontSizeSp = fontSizeSp,
                    )
                }
                is BlockMath -> {
                    BlockMathRenderer(
                        blockMath = contentBlock
                    )
                }
                is ContentList -> {
                    ListRenderer(
                        currentNoteId = currentNoteId,
                        contentList = contentBlock,
                        fontSizeSp = fontSizeSp
                    )
                }
            }
            if (index != contentBlocks.size - 1) {
                SpacerBetweenBlocks(fontSizeSp)
            }
        }
    }
}