package com.mono9rome.typst_note_app.render

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.context.raise
import arrow.core.raise.recover
import com.mono9rome.typst_note_app.OutputContent
import com.mono9rome.typst_note_app.SourceCode
import com.mono9rome.typst_note_app.model.*
import com.mono9rome.typst_note_app.parser.parseToTextBlocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import uniffi.rust_core.renderMathToPng

@Inject
class RustMathRenderer : MathRenderer {
    override suspend fun renderToPng(
        sourceCode: SourceCode,
        textSizeSp: Float,
    ): OutputContent? {
        // 暫定
        val textBlocks = parseToTextBlocks(sourceCode.value)

        val renderedInlineText = textBlocks.render(textSizeSp)

        return renderedInlineText?.let(::OutputContent)
    }

    private suspend fun List<TextBlock>.render(textSizeSp: Float): List<RenderedTextBlock>? =
        recover({
            this@render.map { textBlock ->
                when (textBlock) {
                    is TextBlock.Inline -> {
                        RenderedTextBlock.Inline(textBlock.text.render(textSizeSp))
                    }
                    is TextBlock.Math -> {
                        RenderedTextBlock.Math(
                            source = textBlock.content,
                            content = mathToPng(textBlock.content, textSizeSp)
                        )
                    }
                }
            }
        }) { _ -> null }

    context(_: Raise<Err>)
    private suspend fun InlineText.render(textSizeSp: Float): RenderedInlineText =
        this@render.map { fragment ->
            when (fragment) {
                is InlineTextFragment.Plane -> {
                    RenderedInlineTextFragment.Plane(fragment.content)
                }

                is InlineTextFragment.Math -> {
                    RenderedInlineTextFragment.Math(
                        source = fragment.content,
                        content = mathToPng(fragment.content, textSizeSp)
                    )
                }
            }
        }


    context(_: Raise<Err>)
    private suspend fun mathToPng(
        mathCode: String,
        textSizeSp: Float,
    ): ByteArray = catch({
        withContext(Dispatchers.IO) {
            val textSizePt = convertSpToTypstPt(textSizeSp)
            renderMathToPng(
                mathCode = "#set page(width: auto, height: auto, margin: 1pt, fill: none)\n#set text(size: ${textSizePt}pt)\n#v(0.5em)\n$mathCode\n#v(0.5em)"
            )
        }
    }) { e ->
        raise(Err("${e.message}"))
    }

    private fun convertSpToTypstPt(sp: Float): Float =
        sp * (1f / 3f)
}