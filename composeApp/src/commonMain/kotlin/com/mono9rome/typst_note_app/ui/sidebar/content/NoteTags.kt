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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.component.SimpleTextField
import com.mono9rome.typst_note_app.ui.editor.indicatorHeight
import com.mono9rome.typst_note_app.ui.editor.tabsHeight
import com.mono9rome.typst_note_app.ui.tabBackgroundColor

@Composable
fun NoteTags(modifier: Modifier = Modifier) {
    val viewModel = LocalAppComponent.current.let {
        viewModel { it.noteTagsViewModelProvider() }
    }
    val uiState by viewModel.uiState.collectAsState()
    NoteTagsBody(
        query = uiState.query,
        result = uiState.result,
        onReload = viewModel::load,
        onAddNewTag = viewModel::addNewTag,
        onQueryChange = viewModel::runTagSearch,
        modifier = modifier,
    )
}

@Composable
fun NoteTagsBody(
    query: String,
    result: List<Note.Tag>,
    onQueryChange: (String) -> Unit,
    onReload: () -> Unit,
    onAddNewTag: (Note.Tag.Name) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TagTools(
            onReload = onReload,
            onAddNewTag = onAddNewTag,
        )
        SearchField(
            query = query,
            onQueryChange = onQueryChange,
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
    onReload: () -> Unit,
    onAddNewTag: (Note.Tag.Name) -> Unit,
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