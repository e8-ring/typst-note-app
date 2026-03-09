package com.mono9rome.typst_note_app.ui.sidebar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.model.Note

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
            .padding(horizontal = 16.dp, vertical = 4.dp), // VSCodeっぽい適度な余白
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ファイルアイコン
        Icon(
            imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
            contentDescription = "File Icon",
            modifier = Modifier
                .size(12.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        // ファイル名
        Text(
            text = if (noteMetadata.title != null) noteMetadata.title.value else noteMetadata.id.value,
            style = LocalTextStyle.current.copy(
                fontSize = 12.sp,
                lineHeight = 12.sp,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            )
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun FileItemPreview() {
    Box(modifier = Modifier.padding(30.dp)) {
        Row(
            modifier = Modifier
                .background(Color.LightGray)
                .clickable(onClick = { }) // クリック処理
                .padding(horizontal = 16.dp), // VSCodeっぽい適度な余白
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ファイルアイコン
            Icon(
                imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                contentDescription = "File Icon",
                modifier = Modifier
                    .size(10.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            // ファイル名
            Text(
                text = "Monoidal category",
                fontSize = 12.sp
            )
        }
    }
}
