import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "Sim800"
version = "1.0-SNAPSHOT"
val kotlinVersion = "1.3.0"

plugins {
    application
    kotlin("jvm") version "1.3.0"
    idea
}

application {
    mainClassName = "sim800.kotlin.AppKt"
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib", kotlinVersion))
    compile("org.bidib.com.pi4j:pi4j-core:1.2.M1")
    compile("com.fazecast:jSerialComm:[2.0.0,3.0.0)")
    compile("io.reactivex.rxjava2:rxkotlin:2.3.0")
    compile("io.jenetics:jpx:1.4.0")
    compile("com.google.code.gson:gson:2.8.2")
    compile("com.github.ajalt:clikt:1.6.0")
    compile(kotlin("reflect"))
}
