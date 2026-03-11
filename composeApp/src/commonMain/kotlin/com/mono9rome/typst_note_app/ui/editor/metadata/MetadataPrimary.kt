package com.mono9rome.typst_note_app.ui.editor.metadata

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.component.SimpleTextField

@Composable
fun MetadataPrimary(
    expanded: Boolean,
    currentNoteId: Note.Id,
    currentNoteTitle: Note.Title?,
    lastUpdatedDate: Note.Timestamp,
    onToggleClick: () -> Unit,
    changeTitle: (Note.Title) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "",
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .clickable(
                    onClick = onToggleClick,
                )
        )
        Text(
            text = currentNoteId.value,
            modifier = Modifier
                .padding(horizontal = 8.dp),
            fontSize = 10.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        SimpleTextField(
            enteredText = currentNoteTitle?.value ?: "",
            onValueChange = {
                changeTitle(Note.Title(it))
            },
            modifier = Modifier.weight(1f),
            placeholderText = "title..."
        )
        Text(
            text = "Last update: ",
            modifier = Modifier
                .padding(start = 8.dp),
            fontSize = 10.sp,
        )
        Text(
            text = lastUpdatedDate.format(Note.Timestamp.PATTERN_DAY_SLASHED),
            fontSize = 10.sp,
        )
    }
}