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
}

