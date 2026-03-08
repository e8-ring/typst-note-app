package com.mono9rome.typst_note_app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.ui.container.MainScreenContainer
import com.mono9rome.typst_note_app.ui.editor.Editor
import com.mono9rome.typst_note_app.ui.editor.EditorTabs
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
    val currentNote = uiState.editorState.currentNote
    MainScreenContainer(
        modifier = modifier,
        sidebarContent = {
            NoteChooser(
                onClickItem = viewModel::onSelectFileInChooser
            )
        },
        noteTabContent = {
            if (currentNote != null) {
                EditorTabs(
                    openNotes = uiState.editorState.openNotes,
                    currentNoteId = currentNote.id,
                    onSelectNote = viewModel::onSelectNoteInTabs,
                    onCloseNote = viewModel::closeNote,
                )
            }
        },
        editorContent = {
            if (currentNote != null) {
                Editor(
                    currentNote = currentNote,
                    fontSizeSp = uiState.fontSizeSp,
                    onTitleChange = viewModel::onTitleChange,
                    updateSourceCode = viewModel::onEdited
                )
            }
        },
        viewerContent = {
            if (currentNote != null) {
                ContentViewer(
                    currentNoteId = currentNote.id,
                    fontSizeSp = uiState.fontSizeSp,
                    contentBlocks = uiState.currentRenderedContent,
                )
            }
        }
    )
}