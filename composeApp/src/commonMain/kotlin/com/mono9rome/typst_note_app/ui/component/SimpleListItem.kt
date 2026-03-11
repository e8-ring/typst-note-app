package com.mono9rome.typst_note_app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SimpleListItem(
    itemText: String,
    fontSizeSp: Float,
    iconImageVector: ImageVector,
    iconContentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
    spacerWidth: Dp = 8.dp,
    defaultBackgroundColor: Color = Color.Transparent,
    hoverBackgroundColor: Color = Color.LightGray.copy(alpha = 0.3f),
) {
    // ホバー状態を管理するための InteractionSource
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    // ホバー状態に応じて背景色を変更
    val backgroundColor = if (isHovered) hoverBackgroundColor else defaultBackgroundColor
    SimpleListItemBody(
        itemText = itemText,
        fontSizeSp = fontSizeSp,
        iconImageVector = iconImageVector,
        iconContentDescription = iconContentDescription,
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor) // 背景色を適用
            .hoverable(interactionSource = interactionSource) // ホバー検知
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ), // クリック処理
        paddingValues = paddingValues,
        spacerWidth = spacerWidth,
    )
}

@Composable
fun SimpleListItemBody(
    itemText: String,
    fontSizeSp: Float,
    iconImageVector: ImageVector,
    iconContentDescription: String,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
    spacerWidth: Dp = 8.dp,
) {
    Row(
        modifier = modifier.padding(paddingValues),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // アイコン
        Icon(
            imageVector = iconImageVector,
            contentDescription = iconContentDescription,
            modifier = Modifier
                .size(fontSizeSp.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(spacerWidth))
        // テキスト
        Text(
            text = itemText,
            style = LocalTextStyle.current.copy(
                fontSize = fontSizeSp.sp,
                lineHeight = fontSizeSp.sp,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            )
        )
    }
}