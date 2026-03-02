package com.mono9rome.typst_note_app.render

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.context.raise
import com.mono9rome.typst_note_app.model.Err
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import uniffi.rust_core.renderMathToPng

@Inject
class RustMathRenderer : MathRenderer {

    context(_: Raise<Err>)
    override suspend fun toPng(
        source: String,
        textSizeSp: Float,
    ): MathRepr = catch({
        withContext(Dispatchers.IO) {
            val textSizePt = convertSpToTypstPt(textSizeSp)
            renderMathToPng(
                mathCode = "#set page(width: auto, height: auto, margin: 1pt, fill: none)\n#set text(size: ${textSizePt}pt)\n#v(0.5em)\n$source\n#v(0.5em)"
            ).let(::MathRepr)
        }
    }) { e ->
        raise(Err("${e.message}"))
    }

    private fun convertSpToTypstPt(sp: Float): Float =
        sp * (1f / 3f)
}