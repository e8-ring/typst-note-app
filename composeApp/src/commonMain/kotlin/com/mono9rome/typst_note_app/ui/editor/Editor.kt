package com.mono9rome.typst_note_app.ui.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.model.SourceCode

@Composable
fun Editor(
    currentNote: Note,
    fontSizeSp: Float,
    onTitleChange: (Note.Id, Note.Title) -> Unit,
    attachTag: (Note.Id, Note.Tag.Name) -> Unit,
    deleteTag: (Note.Id, Note.Tag.Name) -> Unit,
    updateSourceCode: (SourceCode) -> Unit,
    modifier: Modifier = Modifier
) {
    EditorBody(
        currentNote = currentNote,
        textSizeSp = fontSizeSp,
        onTitleChange = onTitleChange,
        attachTag = attachTag,
        deleteTag = deleteTag,
        onEdited = updateSourceCode,
        modifier = modifier
    )
}

@Composable
fun EditorBody(
    currentNote: Note,
    textSizeSp: Float,
    onTitleChange: (Note.Id, Note.Title) -> Unit,
    attachTag: (Note.Id, Note.Tag.Name) -> Unit,
    deleteTag: (Note.Id, Note.Tag.Name) -> Unit,
    onEdited: (SourceCode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        MetadataEditor(
            currentNoteId = currentNote.id,
            currentMetadata = currentNote.metadata,
            onTitleChange = onTitleChange,
            attachTag = attachTag,
            deleteTag = deleteTag
        )
        CodeEditorUI(
            currentNoteId = currentNote.id,
            sourceCode = currentNote.sourceCode,
            fontSizeSp = textSizeSp,
            onEdited = onEdited,
            modifier = Modifier.weight(1f),
        )
    }
}

