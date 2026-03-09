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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
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
    expanded: Boolean = false,
    displayTrailingIcon: Boolean = false,
) {
    // フォーカス状態を監視するための InteractionSource
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

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
            ),
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