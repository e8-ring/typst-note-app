package com.mono9rome.typst_note_app.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.model.Note

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
                modifier = Modifier.padding(horizontal = 8.dp),
                fontSize = 8.sp,
            )
            Spacer(modifier = Modifier.width(8.dp))
            TitleEditField(
                enteredText = enteredText,
                onValueChange = {
                    onTitleChange(currentNoteId, Note.Title(it))
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun TitleEditField(
    enteredText: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = enteredText,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = modifier
            .height(20.dp)
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            ),
        textStyle = TextStyle(
            fontSize = 10.sp,
        ),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
            ) {
                if (enteredText.isBlank()) {
                    Text(
                        text = "title...",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
                innerTextField()
            }
        }
    )
}