package com.mono9rome.typst_note_app.ui.sidebar.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.editor.indicatorHeight
import com.mono9rome.typst_note_app.ui.editor.tabsHeight
import com.mono9rome.typst_note_app.ui.sidebar.FileItem
import com.mono9rome.typst_note_app.ui.sidebar.SidebarViewModel
import com.mono9rome.typst_note_app.ui.tabBackgroundColor

@Composable
fun NoteChooser(
    noteLightList: List<Note.Light>,
    onClickFile: (Note.Id) -> Unit,
    notesManager: SidebarViewModel.NotesManager,
    modifier: Modifier = Modifier
) {
    NoteChooserBody(
        notes = noteLightList,
        onAddNewNote = notesManager::addNewNote,
        onReload = notesManager::refresh,
        onClickFile = onClickFile,
        modifier = modifier,
    )
}

@Composable
fun NoteChooserBody(
    notes: List<Note.Light>,
    onAddNewNote: () -> Unit,
    onReload: () -> Unit,
    onClickFile: (Note.Id) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ToolIcons(
            onAddNewNote = onAddNewNote,
            onReload = onReload
        )
        HorizontalDivider(
            thickness = indicatorHeight.dp,
            color = tabBackgroundColor
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(notes) { note ->
                FileItem(
                    noteMetadata = note,
                    onClick = onClickFile
                )
            }
        }
    }
}

@Composable
fun ToolIcons(
    onAddNewNote: () -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rowHeight = tabsHeight - indicatorHeight
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onAddNewNote,
            modifier = Modifier
                .size((rowHeight - 2).dp)
                .padding(horizontal = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "ノート作成",
                modifier = Modifier.size((rowHeight * 0.8).dp),
            )
        }
        IconButton(
            onClick = onReload,
            modifier = Modifier
                .size((rowHeight - 2).dp)
                .padding(horizontal = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "再読み込み",
                modifier = Modifier.size((rowHeight * 0.8).dp),
            )
        }
    }
}