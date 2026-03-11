package com.mono9rome.typst_note_app.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.activeTextColor
import com.mono9rome.typst_note_app.ui.component.SearchableDropdownMenu
import com.mono9rome.typst_note_app.ui.component.SimpleTextField

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
    allTags: List<Note.Tag.Basic>,
    changeTitle: (Note.Title) -> Unit,
    attachTag: (Note.Tag.Id) -> Unit,
    detachTag: (Note.Tag.Id) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        Row(
            modifier = Modifier.padding(all = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentNoteId.value,
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                fontSize = 10.sp,
            )
            Spacer(modifier = Modifier.width(8.dp))
            TitleChanger(
                currentNoteTitle = currentNoteTitle,
                changeTitle = changeTitle,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "created: ",
                modifier = Modifier
                    .padding(start = 8.dp),
                fontSize = 10.sp,
            )
            Text(
                text = currentNoteCreatedDate.format(Note.Timestamp.PATTERN_DAY_SLASHED),
                modifier = Modifier
                    .padding(end = 8.dp),
                fontSize = 10.sp,
            )
        }
        Row(
            modifier = Modifier.padding(all = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NoteTagViewer(
                currentTags = currentNoteTags,
                detachTag = detachTag,
            )
        }
        Row(
            modifier = Modifier.padding(all = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Add a tag: ",
                fontSize = 10.sp,
            )
            TagSelector(
                allTags = allTags,
                currentNoteTags = currentNoteTags,
                attachTag = attachTag,
            )
        }
    }
}

@Composable
fun TitleChanger(
    currentNoteTitle: Note.Title?,
    changeTitle: (Note.Title) -> Unit,
    modifier: Modifier = Modifier
) {
    SimpleTextField(
        enteredText = currentNoteTitle?.value ?: "",
        onValueChange = {
            changeTitle(Note.Title(it))
        },
        modifier = modifier,
        placeholderText = "title..."
    )
}

@Composable
fun NoteTagViewer(
    currentTags: List<Note.Tag.Basic>,
    detachTag: (Note.Tag.Id) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "tags: ",
            fontSize = 10.sp,
        )
        TagsList(
            currentTags = currentTags,
            detachTag = detachTag,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun TagsList(
    currentTags: List<Note.Tag.Basic>,
    detachTag: (Note.Tag.Id) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        currentTags.forEach { tag ->
            Row(
                modifier = Modifier
                    .padding(all = 2.dp)
                    .border(
                        width = 1.dp,
                        color = activeTextColor,
                        shape = RoundedCornerShape(4.dp),
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tag.name.value,
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    fontSize = 10.sp,
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete ${tag.name.value}",
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(12.dp)
                        .clip(CircleShape)
                        .clickable(
                            onClick = { detachTag(tag.id) },
                        ),
                    tint = activeTextColor
                )
            }
        }
    }
}

@Composable
fun TagSelector(
    allTags: List<Note.Tag.Basic>,
    currentNoteTags: List<Note.Tag.Basic>,
    attachTag: (Note.Tag.Id) -> Unit,
) {
    SearchableDropdownMenu(
        options = allTags.map { it.name.value },
        onOptionSelected = { index, _ ->
            val tag = allTags[index]
            if (!currentNoteTags.contains(tag)) {
                attachTag(tag.id)
            }
        }
    )
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