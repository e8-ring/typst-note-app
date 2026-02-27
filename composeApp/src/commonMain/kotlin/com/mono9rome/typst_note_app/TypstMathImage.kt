package com.mono9rome.typst_note_app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import org.jetbrains.skia.Image.Companion.makeFromEncoded

@Composable
fun TypstMathImage(
    pngBytes: ByteArray,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    // ByteArray を Compose Desktop で描画可能な ImageBitmap に変換
    val imageBitmap = remember(pngBytes) {
        makeFromEncoded(pngBytes).toComposeImageBitmap()
    }

    val horizontalScrollState = rememberScrollState()
    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
        ) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Typst Math Equation",
                contentScale = ContentScale.None,
                colorFilter = ColorFilter.tint(textColor)
            )
        }
        HorizontalScrollbar(
            modifier = Modifier.align(Alignment.BottomCenter),
            adapter = rememberScrollbarAdapter(horizontalScrollState)
        )
    }
}

@Composable
fun DynamicMathText(
    textSizeEm: Float,
    mathImages: Map<String, ByteArray>
) {
    val sampleString = buildAnnotatedString {
        append("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua: ")
        appendInlineContent("eqn")
        append("lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
    }

    // 検証結果からの仮定 : 1 em = Typst 5 pt
    // 予想 : Typst 1 pt = 3 px
    val baseFontSizePx = 15f * textSizeEm // 1 em = ? px

    val inlineContent = mathImages.mapValues { (id, bytes) ->
        val imageBitmap = remember(bytes) {
            makeFromEncoded(bytes).toComposeImageBitmap()
        }

        // px を em に変換
        val heightEm = imageBitmap.height.toFloat() / baseFontSizePx
        val widthEm = imageBitmap.width.toFloat() / baseFontSizePx

        InlineTextContent(
            Placeholder(
                width = widthEm.em,
                height = heightEm.em,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            )
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
                    )
//                    .offset(y = 2.dp),
//                contentScale = ContentScale.None,
//                colorFilter = ColorFilter.tint(Color.Black)
            )
        }
    }

    Text(
        text = sampleString,
        fontSize = textSizeEm.em,
        lineHeight = (textSizeEm * 0.2f + 0.8f).em,
        inlineContent = inlineContent
    )
}