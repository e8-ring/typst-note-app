package com.mono9rome.typst_note_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import arrow.core.raise.recover
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.svg.SvgDecoder
import com.mono9rome.typst_note_app.di.AppComponent
import kotlinx.coroutines.launch

val LocalAppComponent = staticCompositionLocalOf<AppComponent> {
    error("LocalAppComponent not provided")
}

@Composable
fun App(appComponent: AppComponent) {
    CompositionLocalProvider(LocalAppComponent provides appComponent) {
        MaterialTheme {
            setSingletonImageLoaderFactory { context ->
                ImageLoader.Builder(context)
                    .components {
                        add(SvgDecoder.Factory())
                    }
                    .build()
            }

            AppBody()
        }
    }
}

@Composable
fun AppBodySVG() {
    val coroutineScope = rememberCoroutineScope()

    var enteredString by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("Ready") }
    var svgContent: String? by remember { mutableStateOf(null) }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message)
        TextField(
            value = enteredString,
            onValueChange = { enteredString = it },
            label = {
                Text("Enter your note")
            },
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        Button(
            onClick = {
                message = "Waiting for Rust..."
                coroutineScope.launch {
                    svgContent = recover({
                        mathToSvg(enteredString).let {
                            message = "Ready"
                            optimizeTypstSvgForCompose(it)
                        }
                    }) { error ->
                        message = error.message
                        null
                    }
                    println(svgContent)
                }
            }
        ) {
            Text("Call Rust")
        }
        if (svgContent != null) {
            SvgString(svgContent!!)
        }
    }
}