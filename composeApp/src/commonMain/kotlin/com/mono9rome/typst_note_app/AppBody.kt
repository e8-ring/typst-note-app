package com.mono9rome.typst_note_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AppBody(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color.LightGray)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NoteField(
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun AppBodyPreview() {
    AppBody()
}