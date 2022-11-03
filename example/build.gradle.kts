plugins {
    `java-library`
}

extra.set("pomDescription", "Example library used for release testing")

val creekVersion = "0.2.0-SNAPSHOT"
val kafkaVersion : String by extra

dependencies {
    implementation("org.creekservice:creek-service-context:$creekVersion")

    testImplementation("org.creekservice:creek-kafka-streams-test:$creekVersion")
}

// Patch Kafka Streams test jar into main Kafka Streams module to avoid split packages:
modularity.patchModule("kafka.streams", "kafka-streams-test-utils-$kafkaVersion.jar")