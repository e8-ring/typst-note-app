package com.mono9rome.typst_note_app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .offset(y = (-1).dp)
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