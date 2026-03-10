package com.mono9rome.typst_note_app.ui.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent

@Composable
fun SourceEditor(modifier: Modifier = Modifier) {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.sourceEditorViewModelProvider() }
    }
    val uiState by viewModel.uiState.collectAsState()
    if (uiState != null) {
        SourceEditorBody(
            currentNoteId = uiState!!.currentNoteId,
            sourceCode = uiState!!.currentNoteSourceCode,
            fontSizeSp = uiState!!.fontSizeSp,
            onEdited = viewModel::updateSourceCode,
            modifier = modifier
        )
    }
}