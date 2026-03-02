package com.mono9rome.typst_note_app.render

import arrow.core.raise.Raise
import com.mono9rome.typst_note_app.model.Err

interface MathRenderer {
    context(_: Raise<Err>)
    suspend fun toPng(
        source: String,
        textSizeSp: Float,
    ): MathRepr
}