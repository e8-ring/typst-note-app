package com.mono9rome.typst_note_app.ui.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.MainScreenViewModel
import com.mono9rome.typst_note_app.ui.SourceCode

@Composable
fun Editor(
    currentNote: Note?,
    fontSizeSp: Float,
    updateSourceCode: (SourceCode) -> Unit,
    textSizeChanger: (Float?) -> Unit,
    modifier: Modifier = Modifier
) {
    EditorBody(
        currentNote = currentNote,
        textSizeSp = fontSizeSp,
        onEdited = updateSourceCode,
        textSizeChanger = textSizeChanger,
        modifier = modifier
    )
}

@Composable
fun EditorBody(
    currentNote: Note?,
    textSizeSp: Float,
    onEdited: (SourceCode) -> Unit,
    textSizeChanger: (Float?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (currentNote != null) {
            CodeEditorUI(
                currentNoteId = currentNote.metadata.id,
                sourceCode = currentNote.sourceCode,
                fontSizeSp = textSizeSp,
                onEdited = onEdited
            )
            TextField(
                value = textSizeSp.toString(),
                onValueChange = {
                    textSizeChanger(it.toFloatOrNull())
                },
                label = {
                    Text("Font Size")
                }
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

