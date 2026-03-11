package com.mono9rome.typst_note_app.ui.editor.metadata

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.component.SearchableDropdownMenu

@Composable
fun NoteTagAttacher(
    allTags: List<Note.Tag.Basic>,
    currentNoteTags: List<Note.Tag.Basic>,
    attachTag: (Note.Tag.Id) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Add a tag: ",
            fontSize = 10.sp,
            modifier = Modifier.padding(end = 2.dp),
        )
        TagSelector(
            allTags = allTags,
            currentNoteTags = currentNoteTags,
            attachTag = attachTag,
        )
    }
}

@Composable
private fun TagSelector(
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