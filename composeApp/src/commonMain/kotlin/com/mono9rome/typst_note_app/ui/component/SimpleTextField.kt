package com.mono9rome.typst_note_app.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mono9rome.typst_note_app.ui.activeTextColor
import com.mono9rome.typst_note_app.ui.borderColor
import com.mono9rome.typst_note_app.ui.preview.PreviewConfig

@Composable
fun SimpleTextField(
    enteredText: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    singleLine: Boolean = true,
    onEnter: () -> Unit = {},
    onLeave: () -> Unit = {},
    expanded: Boolean = false,
    displayTrailingIcon: Boolean = false,
) {
    // フォーカス状態を監視するための InteractionSource
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    // 一番最初に isFocused = false で呼ばれないようにするためのフラグ
    var hasBeenFocused by remember { mutableStateOf(false) }
    // エンター時にフォーカスを外すが、そのときに onLeave が実行されないようにする
    var hasEnterActionBeenPerformed by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    BasicTextField(
        value = enteredText,
        onValueChange = onValueChange,
        singleLine = singleLine,
        interactionSource = interactionSource,
        modifier = modifier
            .height(20.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(2.dp)
            )
            .border(
                width = 1.dp,
                color = if (isFocused) activeTextColor else borderColor,
            )
            // キーボードの入力を直前で監視する
            .onPreviewKeyEvent { keyEvent ->
                // エンターキー（またはテンキーのエンター）が「押された瞬間」かを判定
                if ((keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter) &&
                    keyEvent.type == KeyEventType.KeyDown
                ) {
                    onEnter()
                    hasEnterActionBeenPerformed = true

                    // フォーカスを外す（キャレットが消える）
                    focusManager.clearFocus()

                    // true を返すことで「このキー操作はここで消費した（改行を入力させない）」と宣言する
                    return@onPreviewKeyEvent true
                }

                // エンターキー以外の操作は false を返し、通常のテキスト入力に回す
                false
            }
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    hasBeenFocused = true
                } else {
                    if (hasBeenFocused) {
                        if (hasEnterActionBeenPerformed) {
                            hasEnterActionBeenPerformed = false
                        } else {
                            onLeave()
                        }
                        hasBeenFocused = false
                    }
                }
            },
        textStyle = TextStyle(
            fontSize = 10.sp
        ),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .offset(y = (-1).dp)
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.weight(1f)
                ) {
                    if (enteredText.isBlank()) {
                        Text(
                            text = placeholderText,
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                    innerTextField()
                }
                if (displayTrailingIcon) {
                    Spacer(Modifier.width(8.dp))

                    // 開閉状態に合わせて回転するアイコン
                    // expanded が true なら 180度（上向き）、false なら 0度（下向き）
                    val rotation by animateFloatAsState(
                        targetValue = if (expanded) 180f else 0f,
                        label = "DropdownArrowRotation"
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "ドロップダウンの開閉",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(18.dp) // 20.dp の高さに収まるように小さめにする
                            .rotate(rotation) // ここでアニメーション回転を適用
                    )
                }
            }
        }
    )
}

@Preview(
    widthDp = 300,
    heightDp = 300,
    showBackground = PreviewConfig.SHOW_BACKGROUND,
)
@Composable
fun SimpleTextFieldPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SimpleTextField(
            enteredText = "Title",
            onValueChange = {},
        )
    }
}