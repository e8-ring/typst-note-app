package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.mono9rome.typst_note_app.model.BlockMath
import org.jetbrains.skia.Image.Companion.makeFromEncoded

@Composable
fun BlockMathRenderer(
    blockMath: BlockMath,
    modifier: Modifier = Modifier
) {
    val imageBitmap = makeFromEncoded(blockMath.content.content).toComposeImageBitmap()

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Typst Block-level Math Equation",
            contentScale = ContentScale.None
        )
    }
}