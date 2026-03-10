package com.mono9rome.typst_note_app.ui.viewer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.ui.viewer.renderer.ContentRenderer

@Composable
fun ContentViewer(modifier: Modifier = Modifier) {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.contentViewerViewModelProvider() }
    }
    val uiState by viewModel.uiState.collectAsState()
    if (uiState != null) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
        ) {
            ContentRenderer(
                currentNoteId = uiState!!.currentNoteId,
                contentBlocks = uiState!!.viewerState.contentBlocks,
                fontSizeSp = uiState!!.fontSizeSp,
            )
        }
    }
}