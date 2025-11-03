import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    compilerOptions.freeCompilerArgs.addAll(
        // 上下文参数
        "-Xcontext-parameters",
        // 平台类
        "-Xexpect-actual-classes",
    )

    androidTarget {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)

            // Crypto
            implementation(libs.cryptography.provider.jdk)
            // Network
            implementation(libs.ktor.client.okhttp)
            // UI
            implementation(libs.glance.appwidget)
            implementation(libs.glance.material3)
            // Utils
            implementation(libs.otp)
            implementation(libs.tess4Android)
        }
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.foundation)
            implementation(libs.compose.material3)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(compose.materialIconsExtended)
            implementation(compose.preview)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.viewmodel.navigation3)

            // Crypto
            implementation(libs.cryptography.core)
            // DateTime
            implementation(libs.kotlinx.datetime)
            // Device
            implementation(libs.alert.kmp)
            // DI
            implementation(project.dependencies.platform(libs.koin.bom))
            api(libs.koin.annotation)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.koin.core)
            implementation(libs.koin.jsr330)
            // Network
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization.kotlinx.json)
            // Serializer
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ksoup)
            // Storage
            implementation(libs.datastore)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            // UI
            implementation(libs.adaptive)
            implementation(libs.adaptive.layout)
            implementation(libs.adaptive.navigation)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.colorpicker)
            implementation(libs.composeIcon.css)
            implementation(libs.composeIcon.simple)
            implementation(libs.composeIcon.tabler)
            implementation(libs.compottie)
            implementation(libs.compottie.dot)
            implementation(libs.haze.materials)
            implementation(libs.materialkolor)
            implementation(libs.htmlconverter)
            implementation(libs.markdown.renderer.m3)
            implementation(libs.navigation3.ui)
            implementation(libs.paging.common)
            implementation(libs.paging.compose)
            implementation(libs.windowManager)
            // Utils
            implementation(libs.sublime.fuzzy)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

            // Crypto
            implementation(libs.cryptography.provider.jdk)
            // Network
            implementation(libs.ktor.client.okhttp)
            // Storage
            implementation(libs.appdirs)
            // Utils
            implementation(libs.otp)
            implementation(libs.tess4j)
        }
        iosMain.dependencies {
            // Crypto
            implementation(libs.cryptography.provider.apple)
            // Network
            implementation(libs.ktor.client.darwin)
        }
    }

    // KSP Common sourceSet
    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }
}

private val punicaVersion = "2.0.1"

android {
    namespace = "org.kiteio.punica"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.kiteio.punica"
        minSdk = 30
        targetSdk = 36
        versionCode = 4
        versionName = punicaVersion
    }
    packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")
        }
    }
    applicationVariants.all {
        buildOutputs.all {
            if (this is ApkVariantOutputImpl) {
                // punica-[Platform]-[VersionName].apk
                outputFileName =
                    "punica-android-${defaultConfig.versionName}.apk"
            }
        }
    }
    splits {
        abi {
            isEnable = true
            reset()
            // noinspection ChromeOsAbiSupport
            include("arm64-v8a")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    // Koin
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspAndroid", libs.koin.ksp.compiler)
    add("kspIosX64", libs.koin.ksp.compiler)
    add("kspIosArm64", libs.koin.ksp.compiler)
    add("kspIosSimulatorArm64", libs.koin.ksp.compiler)
    // Room
    add("kspAndroid", libs.room.compiler)
    add("kspDesktop", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

tasks.matching {
    it.name.startsWith("ksp") && it.name != "kspCommonMainKotlinMetadata"
}.configureEach {
    dependsOn("kspCommonMainKotlinMetadata")
}

compose.desktop {
    application {
        mainClass = "org.kiteio.punica.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Punica"
            packageVersion = punicaVersion
            outputBaseDir = project.file("release")
            modules("jdk.unsupported")
            windows.iconFile = project.file("icons/windows.ico")
        }
        buildTypes.release.proguard {
            isEnabled.set(false)
            configurationFiles.from(project.file("proguard-rules.pro"))
        }
    }
}

buildkonfig {
    packageName = "org.kiteio.punica"
    objectName = "Build"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "appName", "Punica")
        buildConfigField(FieldSpec.Type.STRING, "versionName", punicaVersion)
        buildConfigField(
            FieldSpec.Type.STRING,
            "composeVersion",
            libs.versions.compose.multiplatform.get()
        )
        buildConfigField(FieldSpec.Type.STRING, "organization", "Kiteio")
        buildConfigField(FieldSpec.Type.STRING, "officialWebsite", "https://kiteio.top")
    }
}