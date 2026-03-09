package com.mono9rome.typst_note_app.ui.viewer.renderer

import androidx.lifecycle.ViewModel
import arrow.core.raise.recover
import com.mono9rome.typst_note_app.data.NoteRepository
import com.mono9rome.typst_note_app.model.ContentBlock
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.core.parser.BlockParser
import me.tatarka.inject.annotations.Inject

@Inject
class NestedContentRendererViewModel(
    private val blockParser: BlockParser,
    private val noteRepository: NoteRepository,
) : ViewModel() {

    suspend fun parse(noteId: Note.Id): List<ContentBlock> = recover({
        val note = noteRepository.getSourceCode(noteId)
        val contentBlocks = blockParser.parse(note.value)
        contentBlocks
    }) { emptyList() }
}