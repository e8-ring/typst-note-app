package com.mono9rome.typst_note_app.ui.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mono9rome.typst_note_app.ui.editor.metadata.MetadataEditor

@Composable
fun Editor(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        MetadataEditor()
        SourceEditor(
            modifier = Modifier.weight(1f),
        )
    }
}

