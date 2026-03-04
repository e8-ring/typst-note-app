package com.mono9rome.typst_note_app.ui.sidebar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent

@Composable
fun NoteChooser(
    modifier: Modifier = Modifier
) {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.noteChooserViewModelProvider() }
    }
    NoteChooserBody(
        notes = viewModel.list.collectAsState().value,
        modifier = modifier.fillMaxSize(),
    )
}

@Composable
fun NoteChooserBody(
    notes: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        notes.forEach { note ->
            Text(
                text = note,
            )
        }
    }
}