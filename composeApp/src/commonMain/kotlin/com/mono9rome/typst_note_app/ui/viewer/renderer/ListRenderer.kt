package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.model.BulletList
import com.mono9rome.typst_note_app.model.ContentList
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.model.NumberedList
import com.mono9rome.typst_note_app.ui.preview.PreviewConfig
import com.mono9rome.typst_note_app.ui.preview.SampleData

@Composable
fun ListRenderer(
    currentNoteId: Note.Id,
    contentList: ContentList,
    fontSizeSp: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        contentList.items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
            ) {
                when (contentList) {
                    is BulletList -> {
                        Box(
                            modifier = Modifier
                        ) {
                            Text(
                                text = "・ ",
                                fontSize = (fontSizeSp * 0.8f).sp,
                                lineHeight = (fontSizeSp * 1.5).sp,
                            )
                        }
                    }
                    is NumberedList -> {
                        Box(
                            modifier = Modifier
                        ) {
                            Text(
                                text = "${index + 1}. ",
                                fontSize = fontSizeSp.sp,
                                lineHeight = (fontSizeSp * 1.5).sp,
                            )
                        }
                    }
                }
                ContentRenderer(
                    currentNoteId = currentNoteId,
                    contentBlocks = item.blocks,
                    fontSizeSp = fontSizeSp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(
    widthDp = 700,
    heightDp = PreviewConfig.HEIGHT_DP_HD,
    showBackground = true
)
@Composable
fun BlockMathRendererPreview() {
    ListRenderer(
        currentNoteId = Note.Id("0000"),
        contentList = (SampleData.contentBlocks[1] as ContentList),
        fontSizeSp = SampleData.textSizeSp
    )
}