package com.mono9rome.typst_note_app.ui.viewer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.viewer.renderer.ContentRenderer

@Composable
fun ContentViewer(
    currentNoteId: Note.Id,
    fontSizeSp: Float,
    contentBlocks: List<ContentBlock>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
    ) {
        ContentRenderer(
            currentNoteId = currentNoteId,
            contentBlocks = contentBlocks,
            fontSizeSp = fontSizeSp,
        )
    }
}