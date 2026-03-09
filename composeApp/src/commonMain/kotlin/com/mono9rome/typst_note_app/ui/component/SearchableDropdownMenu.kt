package com.mono9rome.typst_note_app.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.ui.borderColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableDropdownMenu(
    options: List<String>, // 候補となる全体のリスト
    onOptionSelected: (String) -> Unit // 選択されたときのコールバック
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            // ここで好きな高さ（小さめ）に強制的に固定する
                            .height(24.dp)
                            // クリック処理（リップルエフェクトも自動で付きます）
                            .clickable {
                                inputText = selectionOption
                                expanded = false
                                onOptionSelected(selectionOption)
                            }
                            .padding(horizontal = 12.dp, vertical = 2.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = selectionOption,
                            style = LocalTextStyle.current.copy(
                                fontSize = 10.sp,
                                lineHeight = 10.sp,
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both
                                )
                            )
                        )
                    }
                    if (index != filteredOptions.lastIndex) {
                        HorizontalDivider(thickness = 1.dp, color = borderColor)
                    }
                }
            }
        }
    }
}