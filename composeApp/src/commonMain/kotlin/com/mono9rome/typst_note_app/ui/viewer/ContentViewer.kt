package com.mono9rome.typst_note_app.ui.viewer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.ui.viewer.renderer.ContentRenderer

@Composable
fun ContentViewer(
    fontSizeSp: Float,
    contentBlocks: List<ContentBlock>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.Blue
                    )
                )
        ) {
            Box(
                modifier = Modifier.border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.Black
                    )
                )
            ) {
                ContentRenderer(
                    contentBlocks = contentBlocks,
                    textSizeSp = fontSizeSp,
                )
            }
        }
    }
}