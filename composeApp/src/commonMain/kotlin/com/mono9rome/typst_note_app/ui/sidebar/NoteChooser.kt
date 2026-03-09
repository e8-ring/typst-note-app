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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.mono9rome.typst_note_app.ui.editor.indicatorHeight
import com.mono9rome.typst_note_app.ui.editor.tabsHeight
import com.mono9rome.typst_note_app.ui.tabBackgroundColor

@Composable
fun NoteChooser(
    onClickFile: (Note.Id) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.noteChooserViewModelProvider() }
    }
    NoteChooserBody(
        notes = viewModel.list.collectAsState().value,
        onAddNewNote = viewModel::addNewNote,
        onReload = viewModel::reload,
        onClickFile = onClickFile,
        modifier = modifier,
    )
}

@Composable
fun NoteChooserBody(
    notes: List<Note.Light>,
    onAddNewNote: () -> Unit,
    onReload: () -> Unit,
    onClickFile: (Note.Id) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ToolIcons(
            onAddNewNote = onAddNewNote,
            onReload = onReload
        )
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
                    onClick = onClickFile
                )
            }
        }
    }
}

@Composable
fun ToolIcons(
    onAddNewNote: () -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rowHeight = tabsHeight - indicatorHeight
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onAddNewNote,
            modifier = Modifier
                .size((rowHeight - 2).dp)
                .padding(horizontal = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "ノート作成",
                modifier = Modifier.size((rowHeight * 0.8).dp),
            )
        }
        IconButton(
            onClick = onReload,
            modifier = Modifier
                .size((rowHeight - 2).dp)
                .padding(horizontal = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "再読み込み",
                modifier = Modifier.size((rowHeight * 0.8).dp),
            )
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