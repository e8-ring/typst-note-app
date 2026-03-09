package com.mono9rome.typst_note_app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import javax.swing.JOptionPane
import kotlin.system.exitProcess

fun main() = application {

    Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
        val errorMessage = """
            エラーが発生したため、アプリケーションを終了します。
            
            【エラー内容】
            ${throwable.javaClass.simpleName}: ${throwable.message}
        """.trimIndent()

        throwable.printStackTrace()

        // Swing の機能を使ってエラーダイアログを強制表示する
        JOptionPane.showMessageDialog(
            null,
            errorMessage,
            "エラー",
            JOptionPane.ERROR_MESSAGE
        )

        // ダイアログが閉じられたら、プロセスを確実に終了させる
        exitProcess(1)
    }

    val appComponent = JvmAppComponent::class.create()

    Window(
        onCloseRequest = ::exitApplication,
        title = "TypstNoteApp",
    ) {
        App(appComponent = appComponent)
    }
}