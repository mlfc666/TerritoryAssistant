import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.ktx)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)


        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "moe.mlfc.territory.assistant"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "moe.mlfc.territory.assistant"
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "moe.mlfc.territory.assistant.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "moe.mlfc.territory.assistant"
            packageVersion = "1.0.0"
        }
    }
}

val rootDistProvider: Directory = rootProject.layout.projectDirectory.dir("dist")
val androidApkProvider: Provider<Directory> = layout.buildDirectory.dir("outputs/apk/release")
val desktopDistProvider: Provider<Directory> = layout.buildDirectory.dir("compose/binaries/main-release")
val webDistProvider: Provider<Directory> = layout.buildDirectory.dir("dist/wasmJs/productionExecutable")

tasks.register("collectAllDist") {
    group = "distribution"
    description = "收集所有平台的构建产物到项目根目录的 dist 文件夹"

    dependsOn("assembleRelease")
    dependsOn("packageReleaseDistributionForCurrentOS")
    dependsOn("wasmJsBrowserDistribution")

    val fs = project.serviceOf<FileSystemOperations>()
    val rootDist = rootDistProvider
    val androidApk = androidApkProvider
    val desktopDist = desktopDistProvider
    val webDist = webDistProvider

    outputs.dir(rootDist)

    doLast {
        val rootDistFolder = rootDist.asFile
        val androidFolder = androidApk.get().asFile
        val desktopFolder = desktopDist.get().asFile
        val webFolder = webDist.get().asFile

        // 清理并创建目录
        if (rootDistFolder.exists()) {
            rootDistFolder.deleteRecursively()
        }
        rootDistFolder.mkdirs()

        if (androidFolder.exists()) {
            fs.copy {
                from(androidFolder)
                into(rootDistFolder.resolve("android"))
                include("*.apk")
            }
        }

        if (desktopFolder.exists()) {
            fs.copy {
                from(desktopFolder)
                into(rootDistFolder.resolve("desktop"))
                include("**/*.deb", "**/*.msi", "**/*.exe", "**/*.dmg", "**/*.pkg")
                eachFile { path = name }
                includeEmptyDirs = false
            }
        }

        if (webFolder.exists()) {
            fs.copy {
                from(webFolder)
                into(rootDistFolder.resolve("web"))
            }
        }

        println("产物已成功收集至: ${rootDistFolder.absolutePath}")
    }
}