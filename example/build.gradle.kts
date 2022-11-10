plugins {
    `java-library`
}

val creekVersion : String by extra

dependencies {
    implementation("org.creekservice:creek-base-type:$creekVersion")
}
