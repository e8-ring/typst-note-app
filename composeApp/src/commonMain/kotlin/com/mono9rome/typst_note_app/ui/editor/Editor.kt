package com.mono9rome.typst_note_app.ui.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mono9rome.typst_note_app.ui.SourceCode

@Composable
fun Editor(
    updateSourceCode: (SourceCode) -> Unit,
    sourceCode: SourceCode,
    fontSizeSp: Float,
    textSizeChanger: (Float?) -> Unit,
    modifier: Modifier = Modifier
) {
    EditorBody(
        sourceCode = sourceCode,
        textSizeSp = fontSizeSp,
        onEdited = updateSourceCode,
        textSizeChanger = textSizeChanger,
        modifier = modifier
    )
}

@Composable
fun EditorBody(
    sourceCode: SourceCode,
    textSizeSp: Float,
    onEdited: (SourceCode) -> Unit,
    textSizeChanger: (Float?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        CodeEditorUI(
            sourceText = sourceCode.value,
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
    }
}

