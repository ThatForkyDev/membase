plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:20.1.0")
}

tasks {
    build.get().dependsOn(shadowJar)

    shadowJar {
        archiveFileName.set("membase.jar")
        destinationDirectory.set(file("out"))
    }
}
