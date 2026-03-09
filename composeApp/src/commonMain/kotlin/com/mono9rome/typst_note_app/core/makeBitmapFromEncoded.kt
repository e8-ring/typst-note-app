package com.mono9rome.typst_note_app.core

import androidx.compose.ui.graphics.ImageBitmap

expect fun makeBitmapFromEncoded(bytes: ByteArray): ImageBitmap?