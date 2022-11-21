import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
}

group = "com.kuroszdaniel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val mockkVersion = "1.13.2"

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:${mockkVersion}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}