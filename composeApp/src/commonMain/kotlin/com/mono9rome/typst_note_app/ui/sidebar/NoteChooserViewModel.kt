package com.mono9rome.typst_note_app.ui.sidebar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mono9rome.typst_note_app.data.LocalFileManager
import com.mono9rome.typst_note_app.data.NoteRepository
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class NoteChooserViewModel(
    private val fileManager: LocalFileManager,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _list = MutableStateFlow<List<Note.Light>>(emptyList())

    val list: StateFlow<List<Note.Light>> = _list.asStateFlow()

    init {
        reload()
    }

    private fun String.removeTypExtension(): String = this.dropLast(4)

    fun addNewNote() {
        viewModelScope.launch {
            noteRepository.makeNew()
            reload()
        }
    }

    fun reload() {
        viewModelScope.launch {
            _list.update {
                val metadataMap = fileManager.readNoteMetadataMap()
                fileManager.getAll()
                    ?.map {
                        val noteId = Note.Id(it.removeTypExtension())
                        Note.Light(
                            id = noteId,
                            title = metadataMap[noteId]?.title
                        )
                    }
                    ?: emptyList()
            }
        }
    }

}