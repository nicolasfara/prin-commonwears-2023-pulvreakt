plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.publishOnCentral)
}

kotlin {
    target {
        compilations.all {
            kotlinOptions {
                allWarningsAsErrors = true
                freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}

publishOnCentral {
    projectLongName.set("Template Kotlin JVM Project")
    projectDescription.set("A template repository for Kotlin JVM projects")
    repository("https://maven.pkg.github.com/danysk/${rootProject.name}".toLowerCase()) {
        user.set("DanySK")
        password.set(System.getenv("GITHUB_TOKEN"))
    }
    publishing {
        publications {
            withType<MavenPublication> {
                pom {
                    scm {
                        connection.set("git:git@github.com:nicolasfara/${rootProject.name}")
                        developerConnection.set("git:git@github.com:nicolasfara/${rootProject.name}")
                        url.set("https://github.com/nicolasfara/${rootProject.name}")
                    }
                    developers {
                        developer {
                            name.set("Nicolas Farabegoli")
                            email.set("nicolas.farabegoli@gmail.com")
                            url.set("https://www.nicolasfarabegoli.it/")
                        }
                    }
                }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        showCauses = true
        showStackTraces = true
        events(*org.gradle.api.tasks.testing.logging.TestLogEvent.values())
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.bundles.pulvreakt)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.pahoMqtt)
    testImplementation(libs.bundles.kotlin.testing)
}
