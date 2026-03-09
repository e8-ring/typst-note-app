package com.mono9rome.typst_note_app.ui.sidebar.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.component.SimpleTextField
import com.mono9rome.typst_note_app.ui.editor.indicatorHeight
import com.mono9rome.typst_note_app.ui.editor.tabsHeight
import com.mono9rome.typst_note_app.ui.sidebar.SidebarViewModel
import com.mono9rome.typst_note_app.ui.tabBackgroundColor

@Composable
fun NoteTags(
    tagsManager: SidebarViewModel.TagsManager,
    tagSearchManager: SidebarViewModel.SearchManager<Note.Tag>,
) {
    val tagSearchState by tagSearchManager.searchState.collectAsState()
    NoteTagsBody(
        onAddNewTag = tagsManager::addNewTag,
        onReload = tagsManager::refresh,
        enteredText = tagSearchState.query,
        onValueChange = tagSearchManager::run,
        result = tagSearchState.result
    )
}

@Composable
fun NoteTagsBody(
    onAddNewTag: (Note.Tag.Name) -> Unit,
    onReload: () -> Unit,
    enteredText: String,
    onValueChange: (String) -> Unit,
    result: List<Note.Tag>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TagTools(
            onAddNewTag = onAddNewTag,
            onReload = onReload,
        )
        SearchField(
            enteredText = enteredText,
            onValueChange = onValueChange,
            placeholderText = "search..."
        )
        HorizontalDivider(
            thickness = indicatorHeight.dp,
            color = tabBackgroundColor
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(result) { tag ->
                TagItem(tag)
            }
        }
    }
}

@Composable
fun TagTools(
    onAddNewTag: (Note.Tag.Name) -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enteredName by remember { mutableStateOf("") }
    val rowHeight = tabsHeight + 6
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SimpleTextField(
            enteredText = enteredName,
            onValueChange = { enteredName = it },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
        )
        val iconSize = (rowHeight * 0.8)
        IconButton(
            onClick = { onAddNewTag(Note.Tag.Name(enteredName)) },
            modifier = Modifier
                .size(iconSize.dp)
                .padding(horizontal = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "タグ作成",
                modifier = Modifier.size(iconSize.dp),
            )
        }
        IconButton(
            onClick = onReload,
            modifier = Modifier
                .size(iconSize.dp)
                .padding(horizontal = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "再読み込み",
                modifier = Modifier.size(iconSize.dp),
            )
        }
    }
}

@Composable
fun TagItem(
    tag: Note.Tag
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp), // VSCodeっぽい適度な余白
        verticalAlignment = Alignment.CenterVertically
    ) {
        // アイコン
        Icon(
            imageVector = Icons.Default.Tag,
            contentDescription = "Tag Icon",
            modifier = Modifier
                .size(12.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        // ファイル名
        Text(
            text = tag.name.value,
            style = LocalTextStyle.current.copy(
                fontSize = 12.sp,
                lineHeight = 12.sp,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            )
        )
    }
}