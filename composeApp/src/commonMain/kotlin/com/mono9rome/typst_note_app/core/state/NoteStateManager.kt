package com.mono9rome.typst_note_app.core.state

import com.mono9rome.typst_note_app.data.NoteRepository
import com.mono9rome.typst_note_app.di.Singleton
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

/**
 * ローカルの全ノートデータリストの状態を監視するクラス。
 *
 * 注意 : エディタで開いているノートや現在選択中のノートの状態は扱わない。
 * */
@Inject
@Singleton
class NoteStateManager(
    private val noteRepository: NoteRepository,
) {
    private val _allNotes = MutableStateFlow<List<Note.Medium>>(emptyList())
    val allNotes: StateFlow<List<Note.Medium>> = _allNotes.asStateFlow()

    /**
     * ノートディレクトリからノートのメタデータを全て読み込む。
     * アプリ起動時の初期化やノートリストの更新時に使用。
     * @throws java.io.FileNotFoundException 読み込み失敗時
     * */
    suspend fun loadAll() {
        _allNotes.update { noteRepository.getAll() }
    }

    suspend fun addNewNote() {
        noteRepository.makeNew()
        loadAll()
    }
}