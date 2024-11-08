plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "1.7.0"
}

val avro4kVersion = "2.1.1"

dependencies {
    implementation("com.github.avro-kotlin.avro4k:avro4k-core:$avro4kVersion")
}

kotlin {
    jvmToolchain(17)
}