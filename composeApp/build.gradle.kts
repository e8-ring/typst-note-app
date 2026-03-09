import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    compilerOptions {
        freeCompilerArgs.set(listOf("-Xcontext-parameters"))
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.material.icons.extended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            // Core
            implementation(libs.jna)
            implementation(libs.arrow.core)
            // DI
            implementation(libs.kotlin.inject.runtime)
            // Okio
            implementation(libs.okio)
            // kotlinx-serialization
            implementation(libs.kotlinx.serialization.json)
            // SVG
            implementation(libs.coil.compose)
            implementation(libs.coil.svg)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "com.mono9rome.typst_note_app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.mono9rome.typst_note_app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Common
    add("kspCommonMainMetadata", libs.kotlin.inject.compiler)
    // Android
    add("kspAndroid", libs.kotlin.inject.compiler)
    // Desktop
    add("kspJvm", libs.kotlin.inject.compiler)

    debugImplementation(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.mono9rome.typst_note_app.MainKt"

        // JNA に共有ライブラリの検索パスを絶対パスで教える
        val resourcesDir = project.file("src/desktopMain/resources").absolutePath
        jvmArgs("-Djna.library.path=$resourcesDir")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.mono9rome.typst_note_app"
            packageVersion = "1.0.0"
        }
    }
}

// Cargo を呼び出してビルドするタスク
val buildRustDesktop by tasks.registering(Exec::class) {
    group = "rust"
    workingDir = file("../rust_core")
    val os = org.gradle.internal.os.OperatingSystem.current()
    val cargo = when {
        os.isWindows -> "cargo"
        os.isMacOsX -> System.getenv("HOME") + "/.cargo/bin/cargo"
        else -> "cargo"
    }
    commandLine(cargo, "build")
}

val generateUniFFIBindings by tasks.registering(Exec::class) {
    group = "rust"

    dependsOn(buildRustDesktop)
    workingDir = file("../rust_core")

    val os = org.gradle.internal.os.OperatingSystem.current()
    val libName = when {
        os.isWindows -> "rust_core.dll"
        os.isMacOsX -> "librust_core.dylib"
        else -> "librust_core.so"
    }
    val cargo = when {
        os.isWindows -> "cargo"
        os.isMacOsX -> System.getenv("HOME") + "/.cargo/bin/cargo"
        else -> "cargo"
    }
    commandLine(
        cargo,
        "run",
        "--features=uniffi/cli",
        "--bin",
        "uniffi-bindgen",
        "generate",
        "--library",
        "target/debug/$libName",
        "--language",
        "kotlin",
        "--out-dir",
        "../composeApp/src/commonMain/kotlin/com/mono9rome/typst_note_app/core"
    )
}

// ビルドされた共有ライブラリを所定のフォルダにコピーするタスク
val copyRustDesktop by tasks.registering(Copy::class) {
    group = "rust"
    dependsOn(buildRustDesktop) // ビルドが終わってから実行

    from("../rust_core/target/debug/")
    // OS ごとの共有ライブラリファイルだけを抽出
    include("*.dll", "*.dylib", "*.so")

    // コピー先: Desktop アプリの resources フォルダ
    into("src/jvmMain/resources/")
}

// Desktop アプリを実行/ビルドする前に、このタスクを必ず走らせるフック
tasks.named("jvmProcessResources") {
    dependsOn(copyRustDesktop)
}

// Kotlin のコンパイル前に必ずバインディングを生成するフック
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn(generateUniFFIBindings)
}