package com.mono9rome.typst_note_app.core.state

import arrow.core.raise.recover
import com.mono9rome.typst_note_app.data.TagRepository
import com.mono9rome.typst_note_app.di.Singleton
import com.mono9rome.typst_note_app.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

@Inject
@Singleton
class TagStateManager(
    private val tagRepository: TagRepository,
) {
    private val _allTags = MutableStateFlow<List<Note.Tag>>(emptyList())
    val allTags: StateFlow<List<Note.Tag>> = _allTags.asStateFlow()

    /**
     * `tags.json` からタグデータを全て読み込む。
     * アプリ起動時の初期化やタグリストの更新時に使用。
     * @throws java.io.FileNotFoundException `tags.json` が存在しない場合
     * @throws java.io.IOException JSON 処理失敗時
     * */
    suspend fun loadAll() {
        _allTags.update { tagRepository.getAll() }
    }

    suspend fun addNewTag(tagName: Note.Tag.Name) {
        recover({ tagRepository.makeNew(tagName, null) }) { e ->
            // TODO: エラーハンドリング（同名のタグが存在する場合）
            throw IllegalArgumentException(e.message)
        }
        loadAll()
    }

    suspend fun renameTag(tagId: Note.Tag.Id, newName: String) {
        tagRepository.renameTag(tagId, newName)
        loadAll()
    }
}