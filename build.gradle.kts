plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:23.0.0")
//    implementation("junit:junit:4.13.2")
//    implementation("org.assertj:assertj-core:3.23.1")
//    implementation("eu.codearte.catch-exception:catch-exception:2.0")
//    implementation("org.mockito:mockito-core:4.8.0")
//    implementation("com.carrotsearch:junit-benchmarks:0.7.2")
//    implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.0")
//    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0")
//    implementation("nl.jqno.equalsverifier:equalsverifier:2.2.1")
}

tasks {
    build.get().dependsOn(shadowJar)

    shadowJar {
        archiveFileName.set("membase.jar")
        destinationDirectory.set(file("out"))
    }
}
