package com.mono9rome.typst_note_app.ui.editor.metadata

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.activeTextColor
import kotlin.collections.forEach

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
private fun TagsList(
    currentTags: List<Note.Tag.Basic>,
    detachTag: (Note.Tag.Id) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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