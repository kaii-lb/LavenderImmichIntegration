@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.vanniktech.mavenPublish)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "com.kaii.lavender"
version = "2.0.0"

kotlin {
    androidLibrary {
        namespace = "com.kaii.lavender.immichintegration"
        compileSdk = 36
        minSdk = 24

        withJava()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    linuxX64 {
        binaries {
            executable {
                baseName = "ImmichIntegrationLib"
            }
        }
    }

    // jvm {
    //     binaries {
    //         executable {
    //             applicationName = "ImmichIntegrationLibJVM"
    //             mainClass = "com.kaii.lavender.immichintegration.MainKt"
    //         }
    //     }
    //
    //     compilerOptions {
    //         jvmTarget.set(JvmTarget.JVM_17)
    //     }
    // }
    //
    // tasks.named<JavaExec>("runJvm") {
    //     standardInput = System.`in`
    // }


    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.io.core)
            implementation(libs.kotlinx.io.bytestring)
            implementation(libs.kotlinx.io.okio)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.client.logging)
            implementation(libs.androidx.compose.foundation)
        }

        // jvmTest.dependencies {
        //     implementation(libs.kotlin.test)
        // }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        linuxX64Main.dependencies {
            implementation(libs.ktor.client.curl)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

// mavenPublishing {
//     publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
//
//     signAllPublications()
//
//     coordinates(group.toString(), "library", version.toString())
//
//     pom {
//         name = "Lavender Immich Integration"
//         description = "Crossplatform kotlin library for the Immich API"
//         inceptionYear = "2026"
//         url = "https://github.com/kaii-lb/LavenderImmichIntegration/"
//         licenses {
//             license {
//                 name = "Apache License, Version 2.0"
//                 url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
//             }
//         }
//         developers {
//             developer {
//                 id = "kaii-lb"
//                 name = "kaii-lb"
//                 url = "https://github.com/kaii-lb"
//             }
//         }
//     }
// }
