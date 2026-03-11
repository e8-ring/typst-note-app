package com.mono9rome.typst_note_app.ui.sidebar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mono9rome.typst_note_app.core.state.NoteStateManager
import com.mono9rome.typst_note_app.core.state.TagStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class SidebarViewModel(
    private val noteStateManager: NoteStateManager,
    private val tagStateManager: TagStateManager,
) : ViewModel() {

    private val _menuBarState = MutableStateFlow(MenuBarContentType.None)
    val menuBarState: StateFlow<MenuBarContentType> = _menuBarState.asStateFlow()

    fun openMenuBarContent(contentType: MenuBarContentType) {
        _menuBarState.update {
            when (it) {
                contentType -> MenuBarContentType.None
                else -> contentType
            }
        }
        // 切り替えるたびにデータを再読み込みする
        loadAllSidebarData()
    }

    private fun loadAllSidebarData() {
        viewModelScope.launch {
            noteStateManager.loadAll()
            tagStateManager.loadAll()
        }
    }
}