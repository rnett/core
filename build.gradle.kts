plugins {
    java
    kotlin("jvm") version "1.3.11"
    `maven-publish`
    maven
}

group = "com.rnett"
version = "1.3.9"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.0")
    compile(kotlin("reflect"))

    testCompile("junit", "junit", "4.12")
    implementation("com.github.kizitonwose.time:time:1.0.1")
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    compile("org.postgresql:postgresql:42.2.5")
    compile("commons-dbcp:commons-dbcp:1.4")
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

artifacts.add("archives", sourcesJar)

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