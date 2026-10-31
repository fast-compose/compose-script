import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

// The newest engine release, or the one given with -PfastComposeVersion=x.y.z.
val fastComposeVersion: String = providers.gradleProperty("fastComposeVersion")
    .getOrElse(libs.versions.fastCompose.get())

kotlin {
    // Pages load compose-script.js; the name is fixed so it does not depend on the project path.
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("compose-script")
        browser()
        binaries.executable()
    }

    sourceSets {
        wasmJsMain.dependencies {
            implementation("io.github.fast-compose:fast-compose:$fastComposeVersion")
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
        }
    }
}
