plugins {
    // 这样做可以避免在每个子项目的类加载器中多次加载插件
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}