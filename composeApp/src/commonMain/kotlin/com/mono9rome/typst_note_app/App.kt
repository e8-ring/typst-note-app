package com.mono9rome.typst_note_app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.mono9rome.typst_note_app.di.AppComponent

val LocalAppComponent = staticCompositionLocalOf<AppComponent> {
    error("LocalAppComponent not provided")
}

@Composable
fun App(appComponent: AppComponent) {
    CompositionLocalProvider(LocalAppComponent provides appComponent) {
        MaterialTheme {
//            setSingletonImageLoaderFactory { context ->
//                ImageLoader.Builder(context)
//                    .components {
//                        add(SvgDecoder.Factory())
//                    }
//                    .build()
//            }

            AppBody()
        }
    }
}