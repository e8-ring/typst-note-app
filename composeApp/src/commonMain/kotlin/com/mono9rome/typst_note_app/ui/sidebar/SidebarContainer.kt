package com.mono9rome.typst_note_app.ui.sidebar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.mono9rome.typst_note_app.ui.component.ResizeHandle
import com.mono9rome.typst_note_app.ui.contentBackgroundColor

@Composable
fun SidebarContainer(
    menuBarContentType: MenuBarContentType,
    menuBar: @Composable RowScope.() -> Unit,
    content: @Composable BoxScope.(Modifier) -> Unit,
) {
    // UI に反映する「実際の」状態（coerceIn で制限される）
    var sidebarWidth by remember { mutableStateOf(200.dp) }
    // ドラッグ中の「仮想の（制限のない）」状態を保持する変数
    var unconstrainedSidebarWidth by remember { mutableStateOf(200.dp) }

    val density = LocalDensity.current

    Row {
        menuBar()
        if (menuBarContentType != MenuBarContentType.None) {
            Row {
                Box(
                    modifier = Modifier
                        .width(sidebarWidth)
                        .fillMaxHeight()
                        .background(contentBackgroundColor),
                ) {
                    content(Modifier.padding(2.dp))
                }
                ResizeHandle(
                    // ドラッグ開始時に、現在の実際の幅を仮想幅としてスナップショット
                    onDragStart = { unconstrainedSidebarWidth = sidebarWidth },
                    onDrag = { dragAmountPx ->
                        val dragAmountDp = with(density) { dragAmountPx.toDp() }
                        // 仮想幅にドラッグ量を足す（マイナス方向にも無限にいく）
                        unconstrainedSidebarWidth += dragAmountDp
                        // UI に反映する幅は、仮想幅を制限したものにする
                        sidebarWidth = unconstrainedSidebarWidth.coerceIn(150.dp, 400.dp)
                    }
                )
            }
        }
    }
}