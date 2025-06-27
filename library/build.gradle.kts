@file:OptIn(ExperimentalBuildToolsApi::class, ExperimentalKotlinGradlePluginApi::class)

import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "io.github.kotlin"
version = "1.0.0"

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    linuxX64 {
        binaries {
            executable {
                baseName = "ImmichIntegrationLib"
                runTask?.standardInput = System.`in`
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.io.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.ktor.client.logging)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
        val linuxX64Main by getting {
            dependencies {
                implementation(libs.ktor.client.curl)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "com.kaii.lavender.immichintegration"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
//         name = "My library"
//         description = "A library."
//         inceptionYear = "2024"
//         url = "https://github.com/kotlin/multiplatform-library-template/"
//         licenses {
//             license {
//                 name = "XXX"
//                 url = "YYY"
//                 distribution = "ZZZ"
//             }
//         }
//         developers {
//             developer {
//                 id = "XXX"
//                 name = "YYY"
//                 url = "ZZZ"
//             }
//         }
//         scm {
//             url = "XXX"
//             connection = "YYY"
//             developerConnection = "ZZZ"
//         }
//     }
// }
