package com.mono9rome.typst_note_app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.ui.container.MainScreenContainer
import com.mono9rome.typst_note_app.ui.editor.Editor
import com.mono9rome.typst_note_app.ui.sidebar.NoteChooser
import com.mono9rome.typst_note_app.ui.viewer.ContentViewer

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.mainScreenViewModelProvider() }
    }
    val uiState by viewModel.uiState.collectAsState()
    MainScreenContainer(
        modifier = modifier,
        sidebarContent = {
            NoteChooser()
        },
        editorContent = {
            Editor(
                updateSourceCode = viewModel::onEdited,
                sourceCode = uiState.sourceCode,
                fontSizeSp = uiState.fontSizeSp,
                textSizeChanger = viewModel::updateTextSizeSp
            )
        },
        viewerContent = {
            ContentViewer(
                fontSizeSp = uiState.fontSizeSp,
                contentBlocks = uiState.contentBlocks,
            )
        }
    )
}