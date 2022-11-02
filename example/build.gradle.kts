plugins {
    `java-library`
}

val creekVersion : String by extra
val creekTestVersion : String by extra

dependencies {
    implementation("org.creekservice:creek-service-context:$creekVersion")

    testImplementation("org.creekservice:creek-kafka-streams-test:$creekTestVersion")
}