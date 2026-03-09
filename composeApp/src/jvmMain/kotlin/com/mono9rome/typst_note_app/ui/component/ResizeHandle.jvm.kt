package com.mono9rome.typst_note_app.ui.component

import androidx.compose.ui.input.pointer.PointerIcon
import java.awt.Cursor

// デスクトップ環境では、Java AWT のリサイズカーソルを割り当てる
actual val horizontalResizeIcon: PointerIcon = PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR))