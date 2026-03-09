package com.mono9rome.typst_note_app.ui.sidebar

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.sidebar.content.NoteChooser
import com.mono9rome.typst_note_app.ui.sidebar.content.NoteSearch

@Composable
fun Sidebar(
    onClickFile: (Note.Id) -> Unit,
) {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.sidebarViewModelProvider() }
    }
    val uiState by viewModel.uiState.collectAsState()
    SidebarContainer(
        menuBarContentType = uiState.menuBarState,
        menuBar = {
            MenuBar(
                openToolBar = viewModel::openMenuBarContent,
            )
        },
        content = { modifier ->
            SidebarContent(
                sidebarUiState = uiState,
                onClickFile = onClickFile,
                notesManager = viewModel.notesManager,
                searchManager = viewModel.searchManager,
                modifier = modifier
            )
        }
    )
}

@Composable
fun SidebarContent(
    sidebarUiState: SidebarViewModel.UiState,
    onClickFile: (Note.Id) -> Unit,
    notesManager: SidebarViewModel.NotesManager,
    searchManager: SidebarViewModel.SearchManager,
    modifier: Modifier = Modifier
) {
    val noteMediumList = sidebarUiState.noteList
    val noteLightList = noteMediumList.map { it.toLight() }
    when (sidebarUiState.menuBarState) {
        MenuBarContentType.None -> {}
        MenuBarContentType.AllList -> {
            NoteChooser(
                noteLightList = noteLightList,
                onClickFile = onClickFile,
                notesManager = notesManager,
                modifier = modifier
            )
        }
        MenuBarContentType.Search -> {
            NoteSearch(
                searchManager = searchManager,
                onClickFile = onClickFile,
            )
        }
        MenuBarContentType.Tags -> {

        }
        MenuBarContentType.Settings -> {

        }
    }
}