package com.mono9rome.typst_note_app

import android.content.Context
import com.mono9rome.typst_note_app.data.AppStorageDir
import com.mono9rome.typst_note_app.di.AppComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
abstract class AndroidAppComponent(
    @get:Provides val context: Context
) : AppComponent() {
    @Provides
    fun provideAppStorageDir(): AppStorageDir {
        return AppStorageDir(context.filesDir.absolutePath)
    }
}