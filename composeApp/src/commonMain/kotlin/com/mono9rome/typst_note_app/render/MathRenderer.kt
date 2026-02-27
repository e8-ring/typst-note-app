package com.mono9rome.typst_note_app.render

import com.mono9rome.typst_note_app.OutputContent
import com.mono9rome.typst_note_app.SourceCode

interface MathRenderer {
    suspend fun renderToPng(
        sourceCode: SourceCode,
        textSizeSp: Float,
    ): OutputContent?
}