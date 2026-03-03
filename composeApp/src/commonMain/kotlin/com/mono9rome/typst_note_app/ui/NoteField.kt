package com.mono9rome.typst_note_app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.ui.container.ResizerContainer
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
    ResizerContainer(
        leftContentDefaultWidth = 500, // TODO : ちゃんと決める
        leftContentMinWidth = 100,
        leftContentMaxWidth = 700,
        modifier = modifier,
        leftContent = {
            SourceEditor(
                sourceCode = sourceCode,
                textSizeSp = textSizeSp,
                onEdited = onEdited,
                textSizeChanger = textSizeChanger,
            )
        },
        rightContent = {
            OutputViewer(
                textSizeSp = textSizeSp,
                contentBlocks = contentBlocks,
            )
        }
    )
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
                Text("Font Size")
            }
        )
    }
}

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