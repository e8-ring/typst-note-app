package com.mono9rome.typst_note_app.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.component.SimpleTextField

@Composable
fun MetadataEditor(
    currentNoteId: Note.Id,
    currentMetadata: Note.Metadata,
    onTitleChange: (Note.Id, Note.Title) -> Unit,
    modifier: Modifier = Modifier
) {
    val enteredText = currentMetadata.title?.value ?: ""
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
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
                onTitleChange = { _, _ -> }
            )
        }
    }
}