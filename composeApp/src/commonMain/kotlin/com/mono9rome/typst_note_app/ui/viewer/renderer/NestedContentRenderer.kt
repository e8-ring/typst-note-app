package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.borderColor

@Composable
fun NestedContentRenderer(
    rootNoteId: Note.Id,
    noteId: Note.Id,
    fontSizeSp: Float,
    modifier: Modifier = Modifier
) {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.nestedContentRendererViewModelProvider() }
    }
    var contentBlocks by remember { mutableStateOf<List<ContentBlock>>(emptyList()) }

    // 一番外の親のノート id が変化したら状態をリセット
    LaunchedEffect(rootNoteId) {
        println("reset contentBlocks!")
        contentBlocks = emptyList()
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = (fontSizeSp / 2).dp)
            .border(
                width = 0.5.dp,
                color = borderColor
            )
    ) {
        // 描画
        LaunchedEffect(Unit) {
            contentBlocks = viewModel.parse(noteId)
        }
        if (contentBlocks.isNotEmpty()) {
            ContentRenderer(
                currentNoteId = rootNoteId,
                contentBlocks = contentBlocks,
                fontSizeSp = fontSizeSp,
                modifier = Modifier.padding(
                    horizontal = (fontSizeSp / 2).dp,
                ),
            )
        }
    }
}