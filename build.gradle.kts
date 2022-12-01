plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:20.1.0")
//    implementation("junit:junit:4.12")
//    implementation("org.assertj:assertj-core:3.9.1")
//    implementation("eu.codearte.catch-exception:catch-exception:1.4.4")
//    implementation("org.mockito:mockito-core:1.10.19")
//    implementation("com.carrotsearch:junit-benchmarks:0.7.2")
//    implementation("com.fasterxml.jackson.core:jackson-annotations:2.8.8")
//    implementation("com.fasterxml.jackson.core:jackson-databind:2.8.8.1")
//    implementation("nl.jqno.equalsverifier:equalsverifier:2.2.1")
}

tasks {
    build.get().dependsOn(shadowJar)

    shadowJar {
        archiveFileName.set("membase.jar")
        destinationDirectory.set(file("out"))
    }
}
