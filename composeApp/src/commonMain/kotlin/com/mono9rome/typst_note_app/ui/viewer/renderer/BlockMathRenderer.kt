package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.mono9rome.typst_note_app.core.makeBitmapFromEncoded
import com.mono9rome.typst_note_app.model.BlockMath

@Composable
fun BlockMathRenderer(
    blockMath: BlockMath,
    modifier: Modifier = Modifier
) {
    val imageBitmap = makeBitmapFromEncoded(blockMath.content.content)

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Typst Block-level Math Equation",
                contentScale = ContentScale.None
            )
        } else {
            Text(
                text = "Typst Block-level Math Equation",
            )
        }
    }
}