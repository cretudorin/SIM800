
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group  = "Sim868"
version = "1.0-SNAPSHOT"


plugins {
    application
    kotlin("jvm") version "1.3.0"
}

application {
    mainClassName = "sim868.kotlin.AppKt"
}

repositories {
    jcenter()
    mavenCentral()

}

dependencies {
    implementation(kotlin("stdlib", "1.3.0"))
    compile("org.bidib.com.pi4j:pi4j-core:1.2.M1")
    compile("com.fazecast:jSerialComm:[2.0.0,3.0.0)")
    compile("io.reactivex.rxjava2:rxkotlin:2.3.0")
    compile("org.java-websocket:Java-WebSocket:1.3.9")
    compile("io.jenetics:jpx:1.4.0")

}

