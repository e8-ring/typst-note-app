package com.mono9rome.typst_note_app.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    widthDp = PreviewConfig.WIDTH_DP_HD,
    heightDp = PreviewConfig.HEIGHT_DP_HD,
    showBackground = PreviewConfig.SHOW_BACKGROUND,
)
@Composable
fun AppBodyPreview() {
    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
//        NoteFieldBody(
//            sourceCode = noteFieldUiState.sourceCode,
//            textSizeSp = noteFieldUiState.textSizeSp,
//            onEdited = {},
//            textSizeChanger = {},
//            contentBlocks = noteFieldUiState.contentBlocks,
//            modifier = Modifier.fillMaxSize()
//        )
    }
}