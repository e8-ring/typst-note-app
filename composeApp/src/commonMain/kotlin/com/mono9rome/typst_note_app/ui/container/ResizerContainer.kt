package com.mono9rome.typst_note_app.ui.container

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun ResizerContainer(
    leftContentDefaultWidth: Int,
    leftContentMinWidth: Int,
    leftContentMaxWidth: Int,
    modifier: Modifier = Modifier,
    leftContent: @Composable BoxScope.() -> Unit,
    rightContent: @Composable BoxScope.() -> Unit,
) {
    // 実際の表示に使われる、現在のサイドバーの幅
    var sidebarWidth by remember { mutableStateOf(leftContentDefaultWidth.dp) }
    // ドラッグ中のマウスカーソルの「本来の位置」を記録する仮想幅
    var virtualSidebarWidth by remember { mutableStateOf(leftContentDefaultWidth.dp) }

    // ドラッグ量（ピクセル）を dp に変換するための Density
    val density = LocalDensity.current

    // 全体を横並び(Row)にする
    Row(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .width(sidebarWidth) // サイドバーの幅
                .fillMaxHeight()
                .background(Color(0xFFEEEEEE)) // メイン画面と区別する色
        ) {
            leftContent()
        }
        // ドラッグ用の境界線（リサイザー）
        Box(
            modifier = Modifier
                .width(4.dp) // つかみやすいように少し幅を持たせる
                .fillMaxHeight()
                .background(Color.DarkGray)
                .pointerHoverIcon(horizontalResizeIcon)
                .pointerInput(Unit) {
                    // 水平方向のドラッグを検知
                    detectHorizontalDragGestures(
                        // ドラッグ開始時に、仮想幅を現在の実際の幅にリセットする
                        onDragStart = { virtualSidebarWidth = sidebarWidth },
                        // ドラッグ終了時・キャンセル時にも念のため同期しておく
                        onDragEnd = { virtualSidebarWidth = sidebarWidth },
                        onDragCancel = { virtualSidebarWidth = sidebarWidth }
                    ) { change, dragAmount ->
                        // イベントを消費（他のスクロール要素などに影響を与えないようにする）
                        change.consume()

                        // ピクセル単位の移動量を dp に変換
                        val dragAmountDp = with(density) { dragAmount.toDp() }

                        // 仮想幅にそのまま移動量を足す（マイナスにもなり得る）
                        virtualSidebarWidth += dragAmountDp

                        // 新しい幅を、仮想幅を min と max で切り詰めたものとする（coerceIn）
                        sidebarWidth = virtualSidebarWidth.coerceIn(
                            minimumValue = leftContentMinWidth.dp,
                            maximumValue = leftContentMaxWidth.dp
                        )
                    }
                }
        )

        Box(
            modifier = Modifier
                .weight(1f) // 残りの幅をすべて埋める
                .fillMaxHeight()
                .background(Color.White)
        ) {
            rightContent()
        }
    }
}