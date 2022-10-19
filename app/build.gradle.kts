plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    kotlin("kapt") version "1.6.0"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.2.71")

    //For dependency injection - dagger
    implementation("com.google.dagger:dagger:2.43.2")
    kapt("com.google.dagger:dagger-compiler:2.43.2")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:31.0.1-jre")

    // Use the Kotlin test library.
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.3.0")
    testImplementation("io.mockk:mockk:1.10.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.1.3")
    testImplementation("io.kotest:kotest-runner-console-jvm:4.1.3")
    testImplementation("io.kotest:kotest-property-jvm:4.1.3")

}

application {
    // Define the main class for the application.
    mainClass.set("bank.application.AppKt")
}

tasks.test{
    useJUnitPlatform()
}
