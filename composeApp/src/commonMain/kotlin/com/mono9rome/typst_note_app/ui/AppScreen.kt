package com.mono9rome.typst_note_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.ui.component.ResizeHandle
import com.mono9rome.typst_note_app.ui.editor.Editor
import com.mono9rome.typst_note_app.ui.editor.EditorTabs
import com.mono9rome.typst_note_app.ui.sidebar.Sidebar
import com.mono9rome.typst_note_app.ui.viewer.ContentViewer

@Composable
fun AppScreen(modifier: Modifier = Modifier) {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.appScreenViewModelProvider() }
    }
    val isSomeNoteFocused by viewModel.isSomeNoteFocused.collectAsState()

    AppScreenContainer(
        isSomeNoteFocused = isSomeNoteFocused,
        sidebar = {
            Sidebar()
        },
        mainContent = {
            val totalWidthPx = constraints.maxWidth
            Column(modifier = Modifier.fillMaxSize()) {
                EditorTabs()
                NoteContent(
                    totalWidthPx = totalWidthPx,
                    editor = { modifier ->
                        Editor(modifier = modifier)
                    },
                    viewer = { modifier ->
                        ContentViewer(modifier = modifier)
                    }
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun AppScreenContainer(
    isSomeNoteFocused: Boolean,
    sidebar: @Composable RowScope.() -> Unit,
    mainContent: @Composable BoxWithConstraintsScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        // 幅が可変なサイドバー
        sidebar()
        // メイン領域
        if (isSomeNoteFocused) {
            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxHeight()) {
                mainContent()
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun NoteContent(
    totalWidthPx: Int,
    editor: @Composable (Modifier) -> Unit,
    viewer: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    // UI に反映する「実際の」状態（coerceIn で制限される）
    var editorPreviewRatio by remember { mutableFloatStateOf(0.5f) }
    // ドラッグ中の「仮想の（制限のない）」状態を保持する変数
    var unconstrainedRatio by remember { mutableFloatStateOf(0.5f) }

    Row(modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(editorPreviewRatio)
                .fillMaxHeight()
                .background(contentBackgroundColor)
        ) {
            editor(Modifier.padding(8.dp))
        }

        ResizeHandle(
            // ドラッグ開始時に、現在の比率を仮想比率としてスナップショット
            onDragStart = { unconstrainedRatio = editorPreviewRatio },
            onDrag = { dragAmountPx ->
                val ratioChange = dragAmountPx / totalWidthPx.toFloat()
                // 仮想比率にドラッグ量を足す
                unconstrainedRatio += ratioChange
                // UI に反映する比率は制限をかける
                editorPreviewRatio = unconstrainedRatio.coerceIn(0.1f, 0.9f)
            }
        )

        Box(
            modifier = Modifier
                .weight(1f - editorPreviewRatio)
                .fillMaxHeight()
                .background(contentBackgroundColor)
        ) {
            viewer(Modifier.padding(8.dp))
        }
    }
}