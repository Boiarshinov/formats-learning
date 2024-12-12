plugins {

}

val msgpackVersion = "0.9.8"
val jacksonDatabindVersion = "2.15.2"
dependencies {
    implementation("org.msgpack:msgpack-core:$msgpackVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonDatabindVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonDatabindVersion")
    implementation("org.msgpack:jackson-dataformat-msgpack:$msgpackVersion")
}