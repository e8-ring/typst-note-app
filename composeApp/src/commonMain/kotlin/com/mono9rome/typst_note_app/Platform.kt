package com.mono9rome.typst_note_app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform