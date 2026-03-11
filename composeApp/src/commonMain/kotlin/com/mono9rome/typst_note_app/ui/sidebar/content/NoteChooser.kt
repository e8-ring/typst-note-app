package com.mono9rome.typst_note_app.ui.sidebar.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.component.SimpleListItem
import com.mono9rome.typst_note_app.ui.component.SimpleTextField
import com.mono9rome.typst_note_app.ui.editor.indicatorHeight
import com.mono9rome.typst_note_app.ui.editor.tabsHeight
import com.mono9rome.typst_note_app.ui.tabBackgroundColor

@Composable
fun NoteChooser(modifier: Modifier = Modifier) {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.noteChooserViewModelProvider() }
    }
    val uiState by viewModel.uiState.collectAsState()
    var enteredText by remember { mutableStateOf("") }
    NoteChooserBody(
        enteredText = enteredText,
        notes = uiState.map { it.toLight() },
        onAddNewNote = viewModel::addNewNote,
        onReload = viewModel::load,
        onQueryChange = { query ->
            // UI への反映
            enteredText = query
            // 処理
            viewModel.runNoteSearch(enteredText)
        },
        onClickFile = viewModel::setFocusNote,
        modifier = modifier,
    )
}

@Composable
fun NoteChooserBody(
    enteredText: String,
    notes: List<Note.Light>,
    onAddNewNote: () -> Unit,
    onReload: () -> Unit,
    onQueryChange: (String) -> Unit,
    onClickFile: (Note.Id) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ToolIcons(
            onAddNewNote = onAddNewNote,
            onReload = onReload
        )
        SearchField(
            enteredText = enteredText,
            onQueryChange = onQueryChange,
            placeholderText = "search..."
        )
        HorizontalDivider(
            modifier = Modifier.padding(bottom = 2.dp),
            thickness = indicatorHeight.dp,
            color = tabBackgroundColor
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(notes) { note ->
                SimpleListItem(
                    itemText = if (note.title != null) note.title.value else note.id.value,
                    fontSizeSp = 12f,
                    iconImageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                    iconContentDescription = "File Icon",
                    onClick = { onClickFile(note.id) },
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
    val rowHeight = tabsHeight + 6
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
                .size((rowHeight * 0.8).dp)
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
                .size((rowHeight * 0.8).dp)
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

@Composable
fun SearchField(
    enteredText: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
) {
    val rowHeight = tabsHeight + 6
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight.dp),
        contentAlignment = Alignment.Center
    ) {
        SimpleTextField(
            enteredText = enteredText,
            onValueChange = onQueryChange,
            modifier = Modifier
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                ),
            placeholderText = placeholderText
        )
    }
}