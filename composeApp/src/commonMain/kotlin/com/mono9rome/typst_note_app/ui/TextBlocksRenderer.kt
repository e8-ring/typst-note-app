package com.mono9rome.typst_note_app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.mono9rome.typst_note_app.model.RenderedTextBlock
import org.jetbrains.skia.Image.Companion.makeFromEncoded

@Composable
fun TextBlocksRenderer(
    textBlocks: List<RenderedTextBlock>,
    textSizeSp: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        textBlocks.forEach { textBlock ->
            when (textBlock) {
                is RenderedTextBlock.Inline -> {
                    InlineTextRenderer(
                        text = textBlock.text,
                        textSizeSp = textSizeSp,
                        modifier = Modifier.border(1.dp, Color.Gray),
                    )
                }
                is RenderedTextBlock.Math -> {
                    MathBlockRenderer(
                        pngBytes = textBlock.content,
                        modifier = Modifier.border(1.dp, Color.Yellow),
                    )
                }
            }
        }
    }
}

@Composable
fun MathBlockRenderer(
    pngBytes: ByteArray,
    modifier: Modifier = Modifier
) {
    val imageBitmap = makeFromEncoded(pngBytes).toComposeImageBitmap()

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Typst Math Equation",
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.Red
                    )
                ),
            contentScale = ContentScale.None
        )
    }
}