// トップレベル（ルート）の build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // id で指定する場合はバージョンと apply false を記述する
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false
}