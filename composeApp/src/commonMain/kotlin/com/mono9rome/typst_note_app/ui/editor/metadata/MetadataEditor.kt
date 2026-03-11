package com.mono9rome.typst_note_app.ui.editor.metadata

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.borderColor

@Composable
fun MetadataEditor(modifier: Modifier = Modifier) {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.metadataEditorViewModelProvider() }
    }
    val uiState by viewModel.uiState.collectAsState()
    val currentNote = uiState.currentNote
    if (currentNote != null) {
        MetadataEditorBody(
            currentNoteId = currentNote.id,
            currentNoteTitle = currentNote.metadata.title,
            currentNoteTags = currentNote.metadata.tags.map(viewModel::getTagById),
            currentNoteCreatedDate = currentNote.metadata.createdDate,
            lastUpdatedDate = currentNote.metadata.lastUpdatedDate,
            allTags = uiState.allTags,
            changeTitle = viewModel::changeTitle,
            attachTag = viewModel::attachTag,
            detachTag = viewModel::detachTag,
            modifier = modifier
        )
    }
}

@Composable
fun MetadataEditorBody(
    currentNoteId: Note.Id,
    currentNoteTitle: Note.Title?,
    currentNoteTags: List<Note.Tag.Basic>,
    currentNoteCreatedDate: Note.Timestamp,
    lastUpdatedDate: Note.Timestamp,
    allTags: List<Note.Tag.Basic>,
    changeTitle: (Note.Title) -> Unit,
    attachTag: (Note.Tag.Id) -> Unit,
    detachTag: (Note.Tag.Id) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        MetadataPrimary(
            expanded = expanded,
            currentNoteId = currentNoteId,
            currentNoteTitle = currentNoteTitle,
            lastUpdatedDate = lastUpdatedDate,
            onToggleClick = { expanded = !expanded },
            changeTitle = changeTitle,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
        if (expanded) {
            Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp)) {
                HorizontalDivider(
                    modifier = Modifier.padding(bottom = 8.dp),
                    thickness = 1.dp,
                    color = borderColor
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                ) {
                    NoteTagAttacher(
                        allTags = allTags,
                        currentNoteTags = currentNoteTags,
                        attachTag = attachTag,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    CreatedDate(
                        createdDate = currentNoteCreatedDate
                    )
                }
                NoteTagViewer(
                    currentTags = currentNoteTags,
                    detachTag = detachTag,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun CreatedDate(
    createdDate: Note.Timestamp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "created: ",
            fontSize = 10.sp,
        )
        Text(
            text = createdDate.format(Note.Timestamp.PATTERN_DAY_SLASHED),
            fontSize = 10.sp,
        )
    }
}

@Preview(
    widthDp = 500,
    heightDp = 500,
    showBackground = true,
)
@Composable
fun MetadataEditorPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(300.dp),
            contentAlignment = Alignment.Center
        ) {
            MetadataEditor()
        }
    }
}