package com.mono9rome.typst_note_app.render

data class MathRepr(val content: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MathRepr

        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        return content.contentHashCode()
    }
}
