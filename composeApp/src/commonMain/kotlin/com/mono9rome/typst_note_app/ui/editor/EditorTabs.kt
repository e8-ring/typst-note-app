package com.mono9rome.typst_note_app.ui.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.model.Note
import com.mono9rome.typst_note_app.ui.activeTextColor
import com.mono9rome.typst_note_app.ui.inactiveTextColor
import com.mono9rome.typst_note_app.ui.tabBackgroundColor

const val tabsHeight = 24
const val indicatorHeight = 2

@Composable
fun EditorTabs(
    openNotes: List<Note.Light>,
    currentNoteId: Note.Id,
    onSelectNote: (Note.Id) -> Unit,
    onCloseNote: (note: Note.Light, isFocused: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (openNotes.isNotEmpty()) {
        Column(modifier = modifier) {
            // 状態更新の関係で selectedTabIndex == -1 になることがある。
            // その場合、いま openNotes が non empty だから selectedTabIndex は 0 としてよい。
            val selectedTabIndex = openNotes
                .indexOfFirst { it.id == currentNoteId }
                .coerceAtLeast(0)
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 0.dp,
                modifier = Modifier.height(tabsHeight.dp),
                containerColor = tabBackgroundColor,
                divider = {}, // 標準の下線を消す
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(
                            selectedTabIndex = selectedTabIndex,
                        ),
                        color = activeTextColor,
                        height = indicatorHeight.dp
                    )
                },
            ) {
                openNotes.forEach { note ->
                    val selected = note.id == currentNoteId
                    val titleValue =
                        if (note.title?.isBlank() ?: true) note.id.value
                        else note.title.value
                    Tab(
                        selected = selected,
                        onClick = { onSelectNote(note.id) },
                        modifier = Modifier.height(tabsHeight.dp),
                        selectedContentColor = activeTextColor,
                        unselectedContentColor = inactiveTextColor,
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = titleValue,
                                    fontSize = 12.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close ${note.id.value}",
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .clickable(
                                            onClick = { onCloseNote(note, selected) },
                                        )
                                )
                            }

                        }
                    )
                }
            }
        }
    }
}