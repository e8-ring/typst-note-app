package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mono9rome.typst_note_app.model.BlockMath

@Composable
fun BlockMathRenderer(
    blockMath: BlockMath,
    modifier: Modifier = Modifier
) {
//    val imageBitmap = makeFromEncoded(blockMath.content.content).toComposeImageBitmap()
//
//    Box(
//        modifier = modifier.fillMaxWidth(),
//        contentAlignment = Alignment.Center
//    ) {
//        Image(
//            bitmap = imageBitmap,
//            contentDescription = "Typst Block-level Math Equation",
//            modifier = Modifier
//                .border(
//                    border = BorderStroke(
//                        width = 1.dp,
//                        color = Color.Red
//                    )
//                ),
//            contentScale = ContentScale.None
//        )
//    }
}