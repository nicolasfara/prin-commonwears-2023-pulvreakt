plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.android) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.taskTree)
}

val Provider<PluginDependency>.id get() = get().pluginId

allprojects {
    group = "it.nicolasfarabegoli.prin.commonwears"

    with(rootProject.libs.plugins) {
        apply(plugin = kotlinx.serialization.id)
        apply(plugin = dokka.id)
        apply(plugin = gitSemVer.id)
        apply(plugin = kotlin.qa.id)
        apply(plugin = taskTree.id)
    }

    repositories {
        mavenCentral()
        google()
    }
}
