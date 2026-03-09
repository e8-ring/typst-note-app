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
fun MetadataEditor(
    currentNoteId: Note.Id,
    currentMetadata: Note.Metadata,
    onTitleChange: (Note.Id, Note.Title) -> Unit,
    attachTag: (Note.Id, Note.Tag.Name) -> Unit,
    deleteTag: (Note.Id, Note.Tag.Name) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.metadataEditorViewModelProvider() }
    }
    val uiState by viewModel.uiState.collectAsState()
    val enteredText = currentMetadata.title?.value ?: ""
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
            SimpleTextField(
                enteredText = enteredText,
                onValueChange = {
                    onTitleChange(currentNoteId, Note.Title(it))
                },
                modifier = Modifier.weight(1f),
                placeholderText = "title..."
            )
        }
        Row(
            modifier = Modifier.padding(all = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NoteTagViewer(
                currentTags = currentMetadata.tags,
                onDeleteTag = {
                    deleteTag(currentNoteId, it)
                }
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
            SearchableDropdownMenu(
                options = uiState.tagNameList.map { it.value },
                onOptionSelected = { tagNameValue ->
                    val tagName = Note.Tag.Name(tagNameValue)
                    if (!currentMetadata.tags.contains(tagName)) {
                        attachTag(currentNoteId, tagName)
                    }
                }
            )
        }
    }
}

@Composable
fun NoteTagViewer(
    currentTags: List<Note.Tag.Name>,
    onDeleteTag: (Note.Tag.Name) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "tags: ",
            fontSize = 10.sp,
        )
        TagsList(
            currentTags = currentTags,
            onDeleteTag = onDeleteTag,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun TagsList(
    currentTags: List<Note.Tag.Name>,
    onDeleteTag: (Note.Tag.Name) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        currentTags.forEach { tagName ->
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
                    text = tagName.value,
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    fontSize = 10.sp,
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete ${tagName.value}",
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(12.dp)
                        .clip(CircleShape)
                        .clickable(
                            onClick = { onDeleteTag(tagName) },
                        ),
                    tint = activeTextColor
                )
            }
        }
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
            MetadataEditor(
                currentNoteId = Note.Id("0000"),
                currentMetadata = Note.Metadata(
                    title = Note.Title("monoid.def"),
                    tags = emptyList()
                ),
                onTitleChange = { _, _ -> },
                attachTag = { _, _ -> },
                deleteTag = { _, _ -> },
            )
        }
    }
}