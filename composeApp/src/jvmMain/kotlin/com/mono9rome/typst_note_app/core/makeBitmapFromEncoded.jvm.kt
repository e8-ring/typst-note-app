package com.mono9rome.typst_note_app.core

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image.Companion.makeFromEncoded

actual fun makeBitmapFromEncoded(bytes: ByteArray): ImageBitmap? =
    makeFromEncoded(bytes).toComposeImageBitmap()