plugins {
    `java-gradle-plugin`
    alias(libs.plugins.dokka)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.publishOnCentral)
    alias(libs.plugins.multiJvmTesting)
    alias(libs.plugins.taskTree)
}

group = "it.nicolasfarabegoli"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    testImplementation(libs.bundles.kotlin.testing)
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

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}

publishOnCentral {
    projectLongName.set("PulvReAKt in PRIN Commonwears 2023")
    projectDescription.set("Hot-warm-cold game demo for PRIN Commonwears 2023 using PulvReAKt")
    repository("https://maven.pkg.github.com/nicolasfara/${rootProject.name}".lowercase()) {
        user.set("nicolasfara")
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
