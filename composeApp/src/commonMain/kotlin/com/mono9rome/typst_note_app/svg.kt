package com.mono9rome.typst_note_app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.context.raise
import coil3.compose.AsyncImage
import com.mono9rome.typst_note_app.model.Err
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uniffi.rust_core.renderMathToSvg

@Composable
fun SvgString(svgString: String) {
    AsyncImage(
        model = svgString.encodeToByteArray(),
        contentDescription = "math",
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun SvgStringSkia(svgString: String) {
    TypstMathSvg(svgString)
}

context(_: Raise<Err>)
suspend fun mathToSvg(mathCode: String): String = catch({
    withContext(Dispatchers.IO) {
        renderMathToSvg(
            mathCode = "#v(0.5em)\n$mathCode\n#v(0.5em)"
        )
    }
}) { e ->
    raise(Err("${e.message}"))
}