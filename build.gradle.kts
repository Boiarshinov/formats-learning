plugins {
    id("java")
}

group = "dev.boiarshinov"

java.targetCompatibility = JavaVersion.VERSION_17
java.sourceCompatibility = JavaVersion.VERSION_17

subprojects {
    apply(plugin = "java")

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("commons-codec:commons-codec:1.17.1")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks {
        withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }
        withType<JavaExec>().configureEach { defaultCharacterEncoding = "UTF-8" }
        withType<Javadoc>().configureEach { options.encoding = "UTF-8" }
        withType<Test>().configureEach { defaultCharacterEncoding = "UTF-8" }
    }
}

