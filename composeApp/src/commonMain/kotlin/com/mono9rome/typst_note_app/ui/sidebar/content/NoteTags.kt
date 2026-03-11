package com.mono9rome.typst_note_app.ui.sidebar.content

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mono9rome.typst_note_app.LocalAppComponent
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.component.SimpleListItem
import com.mono9rome.typst_note_app.ui.component.SimpleListItemBody
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
    var enteredText by remember { mutableStateOf("") }
    NoteTagsBody(
        enteredText = enteredText,
        result = uiState,
        onReload = viewModel::load,
        onAddNewTag = viewModel::addNewTag,
        onQueryChange = { query ->
            // UI への反映
            enteredText = query
            // 処理
            viewModel.runTagSearch(enteredText)
        },
        renameTag = viewModel::renameTag,
        modifier = modifier,
    )
}

@Composable
fun NoteTagsBody(
    enteredText: String,
    result: List<Note.Tag>,
    onQueryChange: (String) -> Unit,
    onReload: () -> Unit,
    onAddNewTag: (Note.Tag.Name) -> Unit,
    renameTag: (Note.Tag.Id, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TagTools(
            onReload = onReload,
            onAddNewTag = onAddNewTag,
        )
        SearchField(
            enteredText = enteredText,
            onQueryChange = onQueryChange,
            placeholderText = "search..."
        )
        HorizontalDivider(
            modifier = Modifier.padding(bottom = 2.dp),
            thickness = indicatorHeight.dp,
            color = tabBackgroundColor
        )
        TagList(
            result = result,
            renameTag = renameTag,
            modifier = Modifier.weight(1f),
        )
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
                .padding(start = 8.dp, end = 4.dp),
            placeholderText = "create a new tag..."
        )
        val iconSize = (rowHeight * 0.8)
        IconButton(
            onClick = {
                if (enteredName.isBlank()) return@IconButton
                onAddNewTag(Note.Tag.Name(enteredName))
            },
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
fun TagList(
    result: List<Note.Tag>,
    renameTag: (Note.Tag.Id, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // スクロール状態の保持
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val viewPortWidth = maxWidth
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState)
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = viewPortWidth)
                    .width(IntrinsicSize.Max),
            ) {
                result.forEach { tag ->
                    TagItem(
                        tag = tag,
                        renameTag = renameTag,
                        viewPortWidth = viewPortWidth,
                    )
                }
            }
        }
    }
}

@Composable
fun TagItem(
    tag: Note.Tag,
    renameTag: (Note.Tag.Id, String) -> Unit,
    viewPortWidth: Dp,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    if (expanded) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.3f))
                .border(1.dp, Color.LightGray)
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            SimpleListItemBody(
                itemText = tag.name.value,
                fontSizeSp = 12f,
                iconImageVector = Icons.Default.Tag,
                iconContentDescription = "File Icon",
                modifier = Modifier
                    .fillMaxWidth()
                    // ホバーしても背景色を変えないように設定
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { expanded = false }
                    )
            )
            TagNameChanger(
                tagId = tag.id,
                previousName = tag.name,
                onConfirm = { tagId, enteredName ->
                    expanded = false
                    if (enteredName.isNotBlank()) {
                        renameTag(tagId, enteredName)
                    }
                },
                modifier = Modifier.width(viewPortWidth)
            )
        }
    } else {
        SimpleListItem(
            itemText = tag.name.value,
            fontSizeSp = 12f,
            iconImageVector = Icons.Default.Tag,
            iconContentDescription = "Tag Icon",
            onClick = { expanded = true },
        )
    }
}

@Composable
fun TagNameChanger(
    tagId: Note.Tag.Id,
    previousName: Note.Tag.Name,
    onConfirm: (Note.Tag.Id, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var enteredName by remember { mutableStateOf(previousName.value) }
    Row(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "new name: ",
            fontSize = 10.sp,
        )
        SimpleTextField(
            enteredText = enteredName,
            onValueChange = { enteredName = it },
            onEnter = { onConfirm(tagId, enteredName) },
            onLeave = { onConfirm(tagId, enteredName) },
        )
    }
}