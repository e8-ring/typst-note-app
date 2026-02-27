package com.mono9rome.typst_note_app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.model.BulletList
import com.mono9rome.typst_note_app.model.ContentList
import com.mono9rome.typst_note_app.model.NumberedList

@Composable
fun ListRenderer(
    contentList: ContentList,
    textSizeSp: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
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
                                text = "■",
                                fontSize = textSizeSp.sp
                            )
                        }
                    }
                    is NumberedList -> {
                        Box(
                            modifier = Modifier
                        ) {
                            Text(
                                text = "$index.",
                                fontSize = textSizeSp.sp
                            )
                        }
                    }
                }
                ContentRenderer(
                    contentBlocks = item.blocks,
                    textSizeSp = textSizeSp
                )
            }
        }
    }
}