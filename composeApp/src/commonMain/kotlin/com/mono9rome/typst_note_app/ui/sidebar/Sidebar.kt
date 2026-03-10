package com.mono9rome.typst_note_app.ui.sidebar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.ui.sidebar.content.NoteChooser
import com.mono9rome.typst_note_app.ui.sidebar.content.NoteTags

@Composable
fun Sidebar() {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.sidebarViewModelProvider() }
    }
    val menuBarState by viewModel.menuBarState.collectAsState()
    SidebarContainer(
        currentMenuBarContentType = menuBarState,
        menuBar = {
            MenuBar(
                openToolBar = viewModel::openMenuBarContent,
            )
        },
        content = { modifier ->
            SidebarContent(
                currentMenuBarContentType = menuBarState,
                modifier = modifier
            )
        }
    )
}

@Composable
fun SidebarContent(
    currentMenuBarContentType: MenuBarContentType,
    modifier: Modifier = Modifier
) {
    when (currentMenuBarContentType) {
        MenuBarContentType.None -> {}
        MenuBarContentType.AllList -> {
            NoteChooser(modifier = modifier)
        }
        MenuBarContentType.Tags -> {
            NoteTags(modifier = modifier)
        }
        MenuBarContentType.Settings -> {

        }
    }
}