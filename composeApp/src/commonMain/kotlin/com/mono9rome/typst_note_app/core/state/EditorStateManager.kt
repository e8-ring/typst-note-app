package com.mono9rome.typst_note_app.core.state

import arrow.core.raise.recover
import com.mono9rome.typst_note_app.data.NoteRepository
import com.mono9rome.typst_note_app.di.Singleton
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

@Inject
@Singleton
class EditorStateManager(
    private val noteRepository: NoteRepository,
) {

    private val _editorState = MutableStateFlow(EditorState.default)
    val editorState: StateFlow<EditorState> = _editorState.asStateFlow()

    /* --- focusedNote (public) --- */

    /**
     * 指定したノート id をもつノートにフォーカスをセットする。
     * もしノートが開かれていない場合は、タブへの追加も行う。
     * */
    suspend fun setFocus(noteId: Note.Id) {
        // タブにない場合、タブに追加する
        if (isNotYetOpen(noteId)) {
            val note = Note.Light(
                id = noteId,
                title = noteRepository.getMetadata(noteId).title
            )
            addNoteToTab(note)
        }
        // フォーカスをセット
        val selectedNote = recover({ noteRepository.get(noteId) }) { e ->
            // TODO: ノート情報の取得に失敗したときのエラーハンドリング
            println("EditorStateManager.setCurrentNote: ${e.message}")
            return
        }
        updateFocusedNote { selectedNote }
    }

    suspend fun changeFocusedNoteTitle(inputTitle: Note.Title) {
        checkFocusedNow()
        val focusedNote = editorState.value.focusedNote!!

        // 最初にローカルデータを編集
        recover({
            noteRepository.updateTitle(
                noteId = focusedNote.id,
                inputTitle = inputTitle
            )
        }) { _ ->
            // TODO: ローカルデータ更新失敗時のエラーハンドリング
            // 整合性のため終了
            return
        }
        // 次にメモリデータに反映
        updateFocusedNoteTitle(inputTitle)
    }

    suspend fun attachTagToFocusedNote(tagId: Note.Tag.Id) {
        checkFocusedNow()
        val focusedNoteId = editorState.value.focusedNote!!.id

        // 最初にローカルデータを編集
        recover({
            noteRepository.addTag(
                noteId = focusedNoteId,
                tagId = tagId
            )
        }) { _ ->
            // TODO: ローカルデータ更新失敗時のエラーハンドリング
            // 整合性のため終了
            return
        }
        // 次にメモリデータに反映
        updateFocusedNoteTags { it + tagId }
    }

    suspend fun detachTagFromFocusedNote(tagId: Note.Tag.Id) {
        checkFocusedNow()
        val focusedNoteId = editorState.value.focusedNote!!.id

        // 最初にローカルデータを編集
        recover({
            noteRepository.deleteTag(
                noteId = focusedNoteId,
                tagId = tagId
            )
        }) { _ ->
            // TODO: ローカルデータ更新失敗時のエラーハンドリング
            // 整合性のため終了
            return
        }
        // 次にメモリデータに反映
        updateFocusedNoteTags { it - tagId }
    }

    suspend fun updateFocusedNoteSourceCode(source: Note.Source) {
        checkFocusedNow()

        // 最初にローカルデータを編集
        noteRepository.write(
            noteId = editorState.value.focusedNote!!.id,
            content = source.value
        )
        // 次にメモリデータに反映
        updateFocusedNote {
            it?.copy(
                source = source,
            )
        }
    }

    /* --- openNotes (public) --- */

    suspend fun closeNote(note: Note.Light) {
        val openNotes = editorState.value.openNotes
        val newOpenNotes = openNotes - note

        when (newOpenNotes.isEmpty()) {
            true -> clearFocus()
            false -> {
                // フォーカスされているノートを閉じる場合は、
                // 既に開かれている別のノートにフォーカスする
                if (isFocused(note.id)) {
                    val currentNoteIdIndex = openNotes.indexOfFirst { it.id == note.id }
                    val nextFocusedNoteIdIndex = when {
                        currentNoteIdIndex >= 1 -> currentNoteIdIndex.dec()
                        currentNoteIdIndex == 0 -> 1
                        else -> throw IndexOutOfBoundsException("at MainScreenViewModel::closeNote")
                    }
                    val nextFocusedNoteId = openNotes[nextFocusedNoteIdIndex].id

                    setFocus(nextFocusedNoteId)
                }
            }
        }

        updateOpenNotes { newOpenNotes }
    }

    /* --- focusedNote (private) --- */

    private fun isNotYetOpen(noteId: Note.Id): Boolean = !(editorState.value.openNotes.any { it.id == noteId })

    private fun addNoteToTab(note: Note.Light) =
        updateOpenNotes { it + listOf(note) }

    private fun updateFocusedNoteTitle(inputTitle: Note.Title) {
        checkFocusedNow()
        val focusedNoteId = editorState.value.focusedNote!!.id
        val title = if (inputTitle.isBlank()) null else inputTitle

        // focusedNote データに変更を反映
        updateFocusedNote {
            it?.copy(
                metadata = it.metadata.copy(
                    title = title,
                )
            )
        }
        // openNotes データに変更を反映
        updateOpenNotes { list ->
            val openNotes = list.toMutableList()
            val currentNoteIndex = list.indexOfFirst { it.id == focusedNoteId }
            val currentNote = openNotes[currentNoteIndex]
            openNotes[currentNoteIndex] = currentNote.copy(title = title)

            openNotes
        }
    }

    private fun updateFocusedNoteTags(update: (List<Note.Tag.Id>) -> List<Note.Tag.Id>) =
        updateFocusedNote {
            it?.copy(
                metadata = it.metadata.copy(
                    tags = update(it.metadata.tags)
                )
            )
        }

    /* --- openNotes (private) --- */

    private fun clearFocus() = updateFocusedNote { null }

    private fun isFocused(noteId: Note.Id): Boolean =
        editorState.value.focusedNote?.id == noteId

    /* --- common private methods --- */

    private fun updateFocusedNote(updateNote: (Note?) -> Note?) {
        _editorState.update {
            it.copy(
                focusedNote = updateNote(it.focusedNote),
            )
        }
    }

    private fun updateOpenNotes(updateNotes: (List<Note.Light>) -> List<Note.Light>) {
        _editorState.update {
            it.copy(
                openNotes = updateNotes(it.openNotes),
            )
        }
    }

    private fun checkFocusedNow() {
        check(editorState.value.focusedNote != null) {
            "Bug: focusedNote が null である状況での呼び出しが発生"
        }
    }
}