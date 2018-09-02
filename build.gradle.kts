import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.2.51"
    `maven-publish`
    maven
}

group = "com.rnett"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
    implementation("com.github.kizitonwose.time:time:1.0.1")
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    implementation("postgresql:postgresql:9.1-901.jdbc3")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(java.sourceSets["main"].allSource)
}

publishing {
    publications {
        create("default", MavenPublication::class.java) {
            from(components["java"])
            artifact(sourcesJar)
        }
        create("mavenJava", MavenPublication::class.java) {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
    repositories {
        maven {
            url = uri("$buildDir/repository")
        }
    }
}