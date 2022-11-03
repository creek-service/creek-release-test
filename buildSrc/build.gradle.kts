plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("io.github.gradle-nexus:publish-plugin:1.1.0")                           // https://plugins.gradle.org/plugin/io.github.gradle-nexus.publish-plugin
}