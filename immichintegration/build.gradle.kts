@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "io.github.kaii-lb.lavender"
version = "2.1.9"

kotlin {
    android {
        namespace = "io.github.kaii_lb.lavender.immichintegration"
        compileSdk = 37
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

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.core.ktx)
        }

        linuxX64Main.dependencies {
            implementation(libs.ktor.client.curl)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "immichintegration", version.toString())

    pom {
        name = "Lavender Immich Integration"
        description = "A cross platform library for Immich integration"
        inceptionYear = "2025"
        url = "https://github.com/kaii-lb/LavenderImmichIntegration"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        developers {
            developer {
                id = "kaii-lb"
                name = "kaii-lb"
                url = "https://github.com/kaii-lb"
                email = "kaiilbbusiness@gmail.com"
                organization = "kaii-lb"
                organizationUrl = "https://github.com/kaii-lb"
            }
        }

        scm {
            url = "https://github.com/kaii-lb/LavenderImmichIntegration"
            connection = "scm:git:git://github.com/kaii-lb/LavenderImmichIntegration.git"
            developerConnection = "scm:git:ssh://git@github.com/kaii-lb/LavenderImmichIntegration.git"
        }
    }
}

abstract class GenerateSecretsTask : DefaultTask() {
    @get:Input
    abstract val apiKey: Property<String>

    @get:Input
    abstract val serverUrl: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val folder = outputDir.get().asFile
        if (!folder.exists()) folder.mkdirs()

        val configFile = folder.resolve("TestConfig.kt")
        configFile.writeText(
            """
            internal object TestConfig {
                const val API_KEY = "${apiKey.get()}"
                const val SERVER_URL = "${serverUrl.get()}"
            }
            """.trimIndent()
        )
    }
}

val generateTestSecrets = tasks.register<GenerateSecretsTask>("generateTestSecrets") {
    val localProperties = Properties().apply {
        val file = project.rootProject.file("immichintegration/local.properties")
        if (file.exists()) file.inputStream().use { load(it) }
    }

    apiKey.set(localProperties.getProperty("IMMICH_API_KEY") ?: "")
    serverUrl.set(localProperties.getProperty("IMMICH_SERVER_URL") ?: "")

    outputDir.set(layout.buildDirectory.dir("generated/test-secrets/kotlin"))
}

kotlin.sourceSets.commonTest {
    kotlin.srcDir(generateTestSecrets.map { it.outputDir })
}