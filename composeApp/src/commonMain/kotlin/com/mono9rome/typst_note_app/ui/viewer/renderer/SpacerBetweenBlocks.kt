package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SpacerBetweenBlocks(fontSizeSp: Float) {
    Spacer(Modifier.height((fontSizeSp * 0.5).dp))
}