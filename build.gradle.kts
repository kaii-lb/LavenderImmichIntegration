plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    kotlin("plugin.serialization") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0" apply false
}