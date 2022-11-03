plugins {
    java
    jacoco
    `maven-publish`
    signing
    id("com.github.spotbugs") version "5.0.13"                           // https://plugins.gradle.org/plugin/com.github.spotbugs
    id("com.diffplug.spotless") version "6.11.0"                         // https://plugins.gradle.org/plugin/com.diffplug.spotless
    id("pl.allegro.tech.build.axion-release") version "1.14.2"           // https://plugins.gradle.org/plugin/pl.allegro.tech.build.axion-release
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"          // https://plugins.gradle.org/plugin/io.github.gradle-nexus.publish-plugin
    id("com.github.kt3k.coveralls") version "2.12.0"                     // https://plugins.gradle.org/plugin/com.github.kt3k.coveralls
    id("org.javamodularity.moduleplugin") version "1.8.12" apply false   // https://plugins.gradle.org/plugin/org.javamodularity.moduleplugin
}

project.version = scmVersion.version

allprojects {
    apply(plugin = "idea")
    apply(plugin = "java")
    apply(plugin = "checkstyle")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "com.github.spotbugs")

    group = "org.creekservice"

    java {
        withSourcesJar()
        withJavadocJar()

        modularity.inferModulePath.set(false)
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    repositories {
        mavenCentral()

        maven {
            url = uri("https://maven.pkg.github.com/creek-service/*")
            credentials {
                username = "Creek-Bot-Token"
                password = "\u0067hp_LtyvXrQZen3WlKenUhv21Mg6NG38jn0AO2YH"
            }
        }
    }

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(15, TimeUnit.MINUTES)
    }
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.javamodularity.moduleplugin")

    if (!name.startsWith("test-")) {
        apply(plugin = "jacoco")
    }

    project.version = project.parent?.version!!

    extra.apply {
        set("creekVersion", "0.2.0-SNAPSHOT")
        set("actualCreekVersion", "0.2.0-SNAPSHOT")
        set("spotBugsVersion", "4.6.0")         // https://mvnrepository.com/artifact/com.github.spotbugs/spotbugs-annotations
        set("kafkaVersion", "3.3.1")            // https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients

        set("log4jVersion", "2.19.0")           // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core
        set("guavaVersion", "31.1-jre")         // https://mvnrepository.com/artifact/com.google.guava/guava
        set("junitVersion", "5.9.1")            // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
        set("junitPioneerVersion", "1.7.1")     // https://mvnrepository.com/artifact/org.junit-pioneer/junit-pioneer
        set("mockitoVersion", "4.8.1")          // https://mvnrepository.com/artifact/org.mockito/mockito-junit-jupiter
        set("hamcrestVersion", "2.2")           // https://mvnrepository.com/artifact/org.hamcrest/hamcrest-core
    }

    val creekVersion = "0.2.0-SNAPSHOT"
    val kafkaVersion : String by extra
    val guavaVersion : String by extra
    val log4jVersion : String by extra
    val junitVersion: String by extra
    val junitPioneerVersion: String by extra
    val mockitoVersion: String by extra
    val hamcrestVersion : String by extra

    dependencies {
        testImplementation("org.creekservice:creek-test-hamcrest:$creekVersion")
        testImplementation("org.creekservice:creek-test-util:$creekVersion")
        testImplementation("org.creekservice:creek-test-conformity:$creekVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
        testImplementation("org.junit-pioneer:junit-pioneer:$junitPioneerVersion")
        testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
        testImplementation("org.hamcrest:hamcrest-core:$hamcrestVersion")
        testImplementation("com.google.guava:guava-testlib:$guavaVersion")
        testImplementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
        testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:$log4jVersion")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    }

    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.apache.kafka") {
                // Need a known Kafka version for module patching to work:
                useVersion(kafkaVersion)
            }
        }
    }

    tasks.compileJava {
        options.compilerArgs.add("-Xlint:all,-serial,-requires-automatic,-requires-transitive-automatic")
        options.compilerArgs.add("-Werror")
    }

    tasks.test {
        useJUnitPlatform()
        setForkEvery(1)
        maxParallelForks = 4
        testLogging {
            showStandardStreams = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showCauses = true
            showExceptions = true
            showStackTraces = true
        }
    }

    spotless {
        java {
            googleJavaFormat("1.12.0").aosp()
            indentWithSpaces()
            importOrder()
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }

    spotbugs {
        tasks.spotbugsMain {
            reports.create("html") {
                required.set(true)
                setStylesheet("fancy-hist.xsl")
            }
        }
        tasks.spotbugsTest {
            reports.create("html") {
                required.set(true)
                setStylesheet("fancy-hist.xsl")
            }
        }
    }

    tasks.withType<JacocoReport>().configureEach{
        dependsOn(tasks.test)
    }

    tasks.javadoc {
        if(JavaVersion.current().isJava9Compatible) {
            (options as StandardJavadocDocletOptions).apply {
                addBooleanOption("html5", true)
            }
        }
    }

    tasks.register("format") {
        dependsOn("spotlessCheck", "spotlessApply")
    }

    tasks.register("static") {
        dependsOn("checkstyleMain", "checkstyleTest", "spotbugsMain", "spotbugsTest")
    }

    if (!project.name.startsWith("test-")) {
        publishing {
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/creek-service/${rootProject.name}")
                    credentials {
                        username = System.getenv("GITHUB_ACTOR")
                        password = System.getenv("GITHUB_TOKEN")
                    }
                }
            }
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["java"])
                    artifactId = "${rootProject.name}-${project.name}"

                    pom {
                        name.set("${project.group}:${artifactId}")

                        description.set("A library used for the testing of the release process")

                        url.set("https://www.creekservice.org")

                        licenses {
                            license {
                                name.set("The Apache License, Version 2.0")
                                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }

                        developers {
                            developer {
                                name.set("Andy Coates")
                                email.set("8012398+big-andy-coates@users.noreply.github.com")
                                organization.set("Creek-Service")
                                organizationUrl.set("https://www.creekservice.org")
                            }
                        }

                        scm {
                            connection.set("scm:git:git://github.com/creek-service/${rootProject.name}.git")
                            developerConnection.set("scm:git:ssh://github.com/creek-service/${rootProject.name}.git")
                            url.set("http://github.com/creek-service/${rootProject.name}")
                        }
                    }
                }
            }
        }

        signing {
            setRequired {
                (!version.toString().endsWith("-SNAPSHOT")) && gradle.taskGraph.hasTask("publishToSonatype")
            }
            if (project.hasProperty("signingKey")) {
                useInMemoryPgpKeys(properties["signingKey"].toString(), properties["signingPassword"].toString())
            }
            sign(publishing.publications["mavenJava"])
        }
    }
}

val coverage = tasks.register<JacocoReport>("coverage") {
    group = "coverage"
    description = "Generates an aggregate code coverage report from all subprojects"

    val coverageReportTask = this

    // If a subproject applies the 'jacoco' plugin, add the result it to the report
    subprojects {
        val subproject = this
        subproject.plugins.withType<JacocoPlugin>().configureEach {
            subproject.tasks.matching({ it.extensions.findByType<JacocoTaskExtension>() != null }).configureEach {
                sourceSets(subproject.sourceSets.main.get())
                executionData(files(subproject.tasks.withType<Test>()).filter { it.exists() && it.name.endsWith(".exec") })
            }

            subproject.tasks.matching({ it.extensions.findByType<JacocoTaskExtension>() != null }).forEach {
                coverageReportTask.dependsOn(it)
            }
        }
    }

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

coveralls {
    sourceDirs = subprojects.flatMap{it.sourceSets.main.get().allSource.srcDirs}.map{it.toString()}
    jacocoReportPath = "$buildDir/reports/jacoco/coverage/coverage.xml"
}

tasks.coveralls {
    group = "coverage"
    description = "Uploads the aggregated coverage report to Coveralls"

    dependsOn(coverage)
    onlyIf{System.getenv("CI") != null}
}

scmVersion {
    checks {
        // Required until https://github.com/allegro/axion-release-plugin/issues/549 fixed
        // As there is a circular test-only dependency between creek-base and creek-test:
        // Todo: while testing need these two:
        snapshotDependencies.set(false)
        uncommittedChanges.set(false)
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            stagingProfileId.set("89a20518f39cd")

            if (project.hasProperty("SONA_USERNAME")) {
                logger.info("Using SONA_USERNAME project property")
                username.set(project.property("SONA_USERNAME").toString())
            } else {
                logger.info("Not using SONA_USERNAME project property")
            }

            if (project.hasProperty("SONA_PASSWORD")) {
                logger.info("Using SONA_PASSWORD project property")
                password.set(project.property("SONA_PASSWORD").toString())
            }
        }
    }
}

defaultTasks("format", "static", "check")

// Todo: drop github packages use?