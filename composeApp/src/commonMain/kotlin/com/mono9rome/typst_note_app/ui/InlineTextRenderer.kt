package com.mono9rome.typst_note_app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.model.RenderedInlineText
import com.mono9rome.typst_note_app.model.RenderedInlineTextFragment
import org.jetbrains.skia.Image.Companion.makeFromEncoded

@Composable
fun InlineTextRenderer(
    text: RenderedInlineText,
    textSizeSp: Float,
    modifier: Modifier = Modifier,
) {
    // インライン数式のリスト
    val inlineMathList = text
       .filterIsInstance<RenderedInlineTextFragment.Math>()

    // 1 em が何 px かを定義
    // 注意 : 1 em は現在指定されている fontSize (sp) のサイズと等しい。
    // 注意 : sp ≒ px.
    val baseFontSizePx = textSizeSp

    // インライン数式からなる Inline Content のリスト
    val inlineContentList = inlineMathList
        .map { fragment ->
            val imageBitmap = makeFromEncoded(fragment.content).toComposeImageBitmap()

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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Typst Math Equation",
                        contentScale = ContentScale.None
                    )
                }
            }
        }

    // インライン数式からなる Inline Content の定義
    val inlineContentMap = inlineMathList
        .mapIndexed { index, fragment ->
            (index.toString() + fragment.source) to inlineContentList[index]
        }
        .toMap()

    // Annotated String の定義
    val annotatedString = buildAnnotatedString {
        text.forEachIndexed { index, fragment ->
            when (fragment) {
                is RenderedInlineTextFragment.Plane -> {
                    append(fragment.content)
                }
                is RenderedInlineTextFragment.Math -> {
                    getInlineContentId(text, index)?.let {
                        appendInlineContent(
                            id = it,
                            alternateText = fragment.source
                        )
                    }
                }
            }
        }
    }

    // UI の描画
    Text(
        text = annotatedString,
        modifier = modifier,
        fontSize = textSizeSp.sp,
        inlineContent = inlineContentMap,
    )
}

// Imperative implementation
private fun getInlineContentId(
    text: RenderedInlineText,
    index: Int
): String? {
    var count = -1
    var restId = ""
    for ((i, fragment) in text.withIndex()) {
        if (fragment is RenderedInlineTextFragment.Math) {
            count++
            if (i == index) {
                // Math でちょうど終われば正常終了
                restId = fragment.source
                break
            }
        } else {
            // Plane で終了することはありえない
            if (i == index) {
                count = -1
                break
            }
        }
    }
    return if (count != -1) {
        count.toString() + restId
    } else null
}