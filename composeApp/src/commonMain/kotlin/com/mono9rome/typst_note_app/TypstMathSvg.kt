package com.mono9rome.typst_note_app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.Data
import org.jetbrains.skia.svg.SVGDOM

@Composable
fun TypstMathSvg(
    svgString: String,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(500.dp)) {
        drawIntoCanvas { canvas ->
            // SVG 文字列をバイト配列にして Skia の Data オブジェクトに変換
            val data = Data.makeFromBytes(svgString.toByteArray())
            // Skia の強力な SVGDOM でパース（<symbol> や <use> も解釈可能）
            val svgDom = SVGDOM(data)

            // Composeのキャンバスサイズに合わせてスケールを調整（必要に応じて）
            val scale = 3f
            canvas.nativeCanvas.scale(scale, scale)

            // Skia のネイティブキャンバスに直接描画
            svgDom.render(canvas.nativeCanvas)
        }
    }
}

/**
 * Skiaで描画できるように、SVG文字列内の <defs> ブロックを先頭に移動させます。
 */
fun preprocessTypstSvgForSkia(svgString: String): String {
    // <defs> ... </defs> を抽出
    val defsRegex = "(<defs.*?>.*?</defs>)".toRegex(RegexOption.DOT_MATCHES_ALL)
    val defsMatch = defsRegex.find(svgString) ?: return svgString
    val defsBlock = defsMatch.value

    // 元の文字列から <defs> ブロックを削除
    val withoutDefs = svgString.removeRange(defsMatch.range)

    // <svg ... > タグの終わりを見つけて、その直後に <defs> を挿入
    val svgTagRegex = "<svg[^>]*>".toRegex()
    val svgTagMatch = svgTagRegex.find(withoutDefs) ?: return svgString

    val insertPos = svgTagMatch.range.last + 1

    return buildString {
        append(withoutDefs.substring(0, insertPos))
        append("\n")
        append(defsBlock)
        append("\n")
        append(withoutDefs.substring(insertPos))
    }
}

fun optimizeTypstSvgForCompose(rawSvgString: String): String {
    // 1. 先ほどの <defs> を先頭に持ってくる処理（省略せずに組み込んでください）
    val defsMovedSvg = preprocessTypstSvgForSkia(rawSvgString)

    return defsMovedSvg
        // 2. <symbol> を汎用的な <g> (グループ) タグに置換する
        .replace("<symbol", "<g")
        .replace("</symbol>", "</g>")

        // 3. 古い形式の xlink:href を、モダンなパーサーが好む href に置換する
        .replace("xlink:href", "href")

        // 4. "pt" 単位によるサイズ計算バグを防ぐため、width と height 属性を削除する
        // （これにより、Compose 側が viewBox "0 0 7.5 9.513" だけを見てよしなにスケーリングしてくれます）
        .replace(Regex("""width="[^"]*""""), "")
        .replace(Regex("""height="[^"]*""""), "")
}

