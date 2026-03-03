package com.mono9rome.typst_note_app

import me.tatarka.inject.annotations.Inject

@Inject
class FontSizeProvider {
    /**
     * sp 単位でのフォントサイズ
     * */
    val current: Float
        get() = 18f
}