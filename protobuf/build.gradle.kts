plugins {
    kotlin("jvm") version "1.8.0"
    id("com.google.protobuf") version "0.9.4"
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.25.5")
    implementation("com.google.protobuf:protobuf-kotlin:3.25.5")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.5"
    }

    generateProtoTasks {
        all().forEach {
            it.builtins {
                // Для дополнительной компиляции kotlin DSL
                create("kotlin")
            }
        }
    }
}

kotlin {
    jvmToolchain(17)
}