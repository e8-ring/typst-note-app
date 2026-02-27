package com.mono9rome.typst_note_app.render

import com.mono9rome.typst_note_app.SourceCode
import com.mono9rome.typst_note_app.model.ContentBlock

interface MathRenderer {
    suspend fun renderToPng(
        sourceCode: SourceCode,
        textSizeSp: Float,
    ): List<ContentBlock>?
}