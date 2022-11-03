plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val jvmTargetVer = 11;

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(jvmTargetVer))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "$jvmTargetVer"
    }
}

dependencies {
    implementation("io.github.gradle-nexus:publish-plugin:1.1.0")                           // https://plugins.gradle.org/plugin/io.github.gradle-nexus.publish-plugin
}