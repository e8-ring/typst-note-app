package com.mono9rome.typst_note_app.ui.sidebar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class MenuBarContentType {
    None, AllList, Tags, Settings
}

@Composable
fun MenuBar(
    openToolBar: (contentType: MenuBarContentType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(40.dp)
            .fillMaxHeight()
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ToolButton(
            onClick = { openToolBar(MenuBarContentType.AllList) },
            imageVector = Icons.Default.Menu,
            contentDescription = "ノート一覧"
        )
        ToolButton(
            onClick = { openToolBar(MenuBarContentType.Tags) },
            imageVector = Icons.Default.Tag,
            contentDescription = "タグ一覧"
        )
        ToolButton(
            onClick = { openToolBar(MenuBarContentType.Settings) },
            imageVector = Icons.Default.Settings,
            contentDescription = "設定"
        )
    }
}

@Composable
private fun ToolButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(32.dp)
            .padding(top = 12.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp)
        )
    }
}