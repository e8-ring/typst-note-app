package com.mono9rome.typst_note_app.ui.sidebar

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.mono9rome.typst_note_app.model.Note

enum class SidebarState {
    None, AllList, Search, Tags, Settings
}

@Composable
fun Sidebar(
    onClickFile: (Note.Id) -> Unit,
) {
    var sidebarState by remember { mutableStateOf(SidebarState.None) }
    SidebarContainer(
        sidebarState = sidebarState,
        menuBar = {
            MenuBar(
                openToolBar = { contentType ->
                    sidebarState = when (sidebarState) {
                        contentType -> SidebarState.None
                        else -> contentType
                    }
                }
            )
        },
        content = { modifier ->
            SidebarContent(
                sidebarState = sidebarState,
                onClickFile = onClickFile,
                modifier = modifier
            )
        }
    )
}

@Composable
fun SidebarContent(
    sidebarState: SidebarState,
    onClickFile: (Note.Id) -> Unit,
    modifier: Modifier = Modifier
) {
    when (sidebarState) {
        SidebarState.None -> {}
        SidebarState.AllList -> {
            NoteChooser(
                onClickFile = onClickFile,
                modifier = modifier
            )
        }
        SidebarState.Search -> {

        }
        SidebarState.Tags -> {

        }
        SidebarState.Settings -> {

        }
    }
}