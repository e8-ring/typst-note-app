package com.mono9rome.typst_note_app.ui.sidebar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.borderColor
import com.mono9rome.typst_note_app.ui.editor.indicatorHeight
import com.mono9rome.typst_note_app.ui.editor.tabsHeight
import com.mono9rome.typst_note_app.ui.tabBackgroundColor

@Composable
fun NoteChooser(
    onClickItem: (Note.Id) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.noteChooserViewModelProvider() }
    }
    NoteChooserBody(
        notes = viewModel.list.collectAsState().value,
        onClickItem = onClickItem,
        modifier = modifier,
    )
}

@Composable
fun NoteChooserBody(
    notes: List<Note.Light>,
    onClickItem: (Note.Id) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height((tabsHeight - indicatorHeight).dp))
        HorizontalDivider(
            thickness = indicatorHeight.dp,
            color = tabBackgroundColor
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(notes) { note ->
                FileItem(
                    noteMetadata = note,
                    onClick = onClickItem
                )
            }
        }
    }
}

@Composable
fun FileItem(
    noteMetadata: Note.Light,
    onClick: (Note.Id) -> Unit
) {
    // ホバー状態を管理するための InteractionSource
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    // ホバー状態に応じて背景色を変更
    // VSCodeっぽく、ホバー時は薄いグレーにする
    val backgroundColor = if (isHovered) Color.LightGray.copy(alpha = 0.3f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor) // 背景色を適用
            .hoverable(interactionSource = interactionSource) // ホバー検知
            .clickable(onClick = { onClick(noteMetadata.id) }) // クリック処理
            .padding(horizontal = 16.dp, vertical = 2.dp), // VSCodeっぽい適度な余白
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ファイルアイコン
        Icon(
            imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
            contentDescription = "File Icon",
            modifier = Modifier.size(12.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        // ファイル名
        Text(
            text = if (noteMetadata.title != null) noteMetadata.title.value else noteMetadata.id.value,
            fontSize = 12.sp
        )
    }
}