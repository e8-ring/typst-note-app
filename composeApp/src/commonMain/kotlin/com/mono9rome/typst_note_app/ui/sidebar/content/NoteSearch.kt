package com.mono9rome.typst_note_app.ui.sidebar.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.component.SimpleTextField
import com.mono9rome.typst_note_app.ui.editor.indicatorHeight
import com.mono9rome.typst_note_app.ui.editor.tabsHeight
import com.mono9rome.typst_note_app.ui.sidebar.FileItem
import com.mono9rome.typst_note_app.ui.sidebar.SidebarViewModel
import com.mono9rome.typst_note_app.ui.tabBackgroundColor

@Composable
fun NoteSearch(
    noteSearchManager: SidebarViewModel.SearchManager<Note.Medium>,
    onClickFile: (Note.Id) -> Unit,
) {
    val noteSearchState by noteSearchManager.searchState.collectAsState()
    NoteSearchBody(
        enteredText = noteSearchState.query,
        onValueChange = noteSearchManager::run,
        result = noteSearchState.result.map { it.toLight() },
        onClickFile = onClickFile
    )
}

@Composable
fun NoteSearchBody(
    enteredText: String,
    onValueChange: (String) -> Unit,
    result: List<Note.Light>,
    onClickFile: (Note.Id) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        SearchField(
            enteredText = enteredText,
            onValueChange = onValueChange,
            placeholderText = "search..."
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
            items(result) { note ->
                FileItem(
                    noteMetadata = note,
                    onClick = onClickFile
                )
            }
        }
    }
}

@Composable
fun SearchField(
    enteredText: String,
    onValueChange: (String) -> Unit,
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
            onValueChange = onValueChange,
            modifier = Modifier
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                ),
            placeholderText = placeholderText
        )
    }
}