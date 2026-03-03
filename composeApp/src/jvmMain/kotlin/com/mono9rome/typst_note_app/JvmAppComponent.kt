package com.mono9rome.typst_note_app

import com.mono9rome.typst_note_app.data.AppStorageDir
import com.mono9rome.typst_note_app.di.AppComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import java.io.File

@Component
abstract class JvmAppComponent : AppComponent() {
    @Provides
    fun provideAppStorageDir(): AppStorageDir {
        val homeDir = System.getProperty("user.home")
        val appDir = "$homeDir/TypstNoteApp"

        File(appDir).mkdirs()

        return AppStorageDir(appDir)
    }
}