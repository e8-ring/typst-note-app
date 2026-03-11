package com.mono9rome.typst_note_app.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mono9rome.typst_note_app.ui.borderColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableDropdownMenu(
    options: List<String>, // 候補となる全体のリスト
    onOptionSelected: (Int, String) -> Unit // 選択されたときのコールバック
) {
    // ドロップダウンが開いているかどうかの状態
    var expanded by remember { mutableStateOf(false) }
    // テキストフィールドに入力されている文字列
    var inputText by remember { mutableStateOf("") }

    // 入力された文字列をもとに、候補リストをフィルタリングする（大文字小文字を区別しない）
    val filteredOptions = options.filter {
        it.contains(inputText, ignoreCase = true)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        // 1. テキストフィールド
        SimpleTextField(
            enteredText = inputText,
            onValueChange = {
                inputText = it
                expanded = true // 文字が入力されたらメニューを開く
            },
            modifier = Modifier
                // 【超重要】この menuAnchor() がないと、メニューがどこに出るか分からずクラッシュします
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
            expanded = expanded,
            displayTrailingIcon = true
        )

        // 2. 絞り込まれた結果を表示するドロップダウンメニュー
        // （候補が1件以上あるときだけ表示する）
        if (filteredOptions.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = Color.White,
                border = BorderStroke(1.dp, borderColor)
            ) {
                filteredOptions.forEachIndexed { index, selectionOption ->
                    SimpleListItem(
                        itemText = selectionOption,
                        fontSizeSp = 10f,
                        iconImageVector = Icons.Default.Tag,
                        iconContentDescription = "Tag Icon",
                        onClick = {
                            inputText = ""
                            expanded = false
                            onOptionSelected(index, selectionOption)
                        },
                        paddingValues = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        spacerWidth = 4.dp
                    )
                }
            }
        }
    }
}