plugins {
    kotlin("jvm") version "1.8.0"
    id("com.google.protobuf") version "0.9.4"
}

val protobufVersion = "3.25.5"
dependencies {
    implementation("com.google.protobuf:protobuf-java:$protobufVersion")
    implementation("com.google.protobuf:protobuf-java-util:$protobufVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
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