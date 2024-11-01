rootProject.name = "formats-learning"
include("protobuf")
include("avro")
include("thrift")
include("message-pack")

pluginManagement {
    plugins {
        kotlin("jvm") version "1.9.22"
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
