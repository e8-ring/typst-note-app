package com.mono9rome.typst_note_app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mono9rome.typst_note_app.OutputContent

@Composable
fun ContentRenderer(
    outputContent: OutputContent,
    textSizeSp: Float,
    modifier: Modifier = Modifier,
) {
    TextBlocksRenderer(
        textBlocks = outputContent.content,
        textSizeSp = textSizeSp,
        modifier = modifier,
    )
}