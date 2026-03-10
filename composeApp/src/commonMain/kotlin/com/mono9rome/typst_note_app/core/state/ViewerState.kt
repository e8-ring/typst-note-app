package com.mono9rome.typst_note_app.core.state

import com.mono9rome.typst_note_app.model.ContentBlock

data class ViewerState(
    val contentBlocks: List<ContentBlock>,
    val isCompileError: Boolean,
) {
    companion object {
        val default = ViewerState(
            contentBlocks = emptyList(),
            isCompileError = false,
        )
    }
}
