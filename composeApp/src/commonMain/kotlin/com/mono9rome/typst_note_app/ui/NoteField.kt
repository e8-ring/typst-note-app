package com.mono9rome.typst_note_app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.ui.renderer.ContentRenderer

@Composable
fun NoteField(
    modifier: Modifier = Modifier
) {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.noteFieldViewModelProvider() }
    }
    val uiState by viewModel.uiState.collectAsState()
    NoteFieldBody(
        sourceCode = uiState.sourceCode,
        textSizeSp = uiState.textSizeSp,
        onEdited = viewModel::onEdited,
        textSizeChanger = viewModel::updateTextSizeSp,
        contentBlocks = uiState.contentBlocks,
        modifier = modifier
    )
}

@Composable
fun NoteFieldBody(
    sourceCode: SourceCode,
    textSizeSp: Float,
    onEdited: (SourceCode) -> Unit,
    textSizeChanger: (Float?) -> Unit,
    contentBlocks: List<ContentBlock>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.Top
    ) {
        SourceEditor(
            sourceCode = sourceCode,
            textSizeSp = textSizeSp,
            onEdited = onEdited,
            textSizeChanger = textSizeChanger,
            modifier = Modifier.weight(0.5f)
        )
        OutputViewer(
            textSizeSp = textSizeSp,
            contentBlocks = contentBlocks,
            modifier = Modifier.weight(0.5f)
        )
    }
}

@Composable
fun SourceEditor(
    sourceCode: SourceCode,
    textSizeSp: Float,
    onEdited: (SourceCode) -> Unit,
    textSizeChanger: (Float?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        TextField(
            value = sourceCode.value,
            onValueChange = {
                onEdited(SourceCode(it))
            },
            label = {
                Text("Enter your note")
            },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = textSizeSp.toString(),
            onValueChange = {
                textSizeChanger(it.toFloatOrNull())
            },
            label = {
                Text("Math Font Size")
            }
        )
    }
}

// 注意 : 現在は sourceCode はフルに数式だとしている
@Composable
fun OutputViewer(
    textSizeSp: Float,
    contentBlocks: List<ContentBlock>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.Blue
                    )
                )
        ) {
            Box(
                modifier = Modifier.border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.Black
                    )
                )
            ) {
                ContentRenderer(
                    contentBlocks = contentBlocks,
                    textSizeSp = textSizeSp,
                )
            }
        }
    }
}