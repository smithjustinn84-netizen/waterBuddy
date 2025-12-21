import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.metro)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kover)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            // Required when using NativeSQLiteDriver
            linkerOpts.add("-lsqlite3")
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation("com.google.android.material:material:1.13.0")
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.sqlite.bundled)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.metro.viewmodel.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
        androidUnitTest.dependencies {
            implementation(libs.kotest.runner.junit5)
            implementation(libs.androidx.testExt.junit)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.example.waterbuddy"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.waterbuddy"
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
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.useJUnitPlatform()
            }
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
}

kover {
    reports {
        filters {
            excludes {
                // 1. Exclude by Annotation (Best for Compose/Generated code)
                annotatedBy(
                    "androidx.compose.runtime.Composable",
                    "androidx.compose.ui.tooling.preview.Preview",
                    "kotlinx.serialization.Serializable",
                    // If you use a custom multiplatform preview annotation
                    "com.yourproject.ui.annotations.MultiPreview"
                )

                classes(
                    "*_Component",
                    "*_Module",
                    "*.ComposableSingletons*",
                    "*ComposableInvoker*",
                    "*ScreenKt*",
                    "*.generated.resources.*",
                    "*Res",
                    "*Res$*",
                    // Android standard exclusions
                    "*.BuildConfig",
                    "*Activity",
                    "*Fragment",
                    "*FragmentArgs",
                    "*FragmentDirections",
                    // Room & Data exclusions
                    "*_Impl*", // Room generated implementations
                    "*.AppDatabase",
                    // DI & Architecture boilerplate
                    "*.di.*",
                    "*ModuleKt*",
                    "*_Factory*", // Metro/Dagger generated
                    // Metro generates Factory classes for every @Inject constructor
                    "*_Factory",
                    "*_Factory$*", // Inner classes like InstanceHolder
                    // Metro generates Provider classes for @Provides methods
                    "*_Provider",
                    // Exclude the generated Dependency Graph implementations
                    "*MetroAppGraph",
                    "*Metro*Graph",
                    "*MetroContributionTo*",
                    "*BindsMirror",
                    // Members injectors (if using field injection in Activities)
                    "*_MembersInjector",
                    // KMP / Platform specific
                    "*.PlatformKt",
                    "*ThemeKt*",       // Excludes Theme.kt files
                    "*TypographyKt*",  // Excludes Typography.kt files
                    "*ColorsKt*",      // Excludes Colors.kt files
                    "*ShapesKt*",      // Excludes Shapes.kt files
                    "*ColorKt*",       // Excludes Color.kt files
                    "*TypeKt*",        // Excludes Type.kt files
                    "*ShapeKt*",       // Excludes Shape.kt files
                    "*ComposableSingletons*", // Internal Compose artifacts
                    $$"*$MetroFactory*",
                    $$"*$MetroFactory$*"
                )
            }
        }

        verify {
            rule {
                bound {
                    minValue = 100
                }
            }
        }
    }
}
