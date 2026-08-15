plugins {
    id("java")
    id("application")
}

group = "berries.apps"
version = "1.0-alpha-3"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.slf4j:slf4j-api:2.0.18")
    implementation("org.slf4j:slf4j-simple:2.0.18")
    implementation("com.fasterxml.jackson.core:jackson-core:2.21.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.21")

    implementation("io.netty:netty-all:4.2.13.Final")
    implementation("io.netty:netty-handler:4.2.13.Final")
    implementation("io.netty:netty-buffer:4.2.13.Final")
    implementation("io.netty:netty-transport:4.2.13.Final")

    implementation("com.google.guava:guava:33.6.0-jre")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "berries.apps.nssl.Bootstrap"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("lssl-${project.version}.jar")
}

tasks.test {
    useJUnitPlatform()
}