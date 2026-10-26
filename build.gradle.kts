plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.3.10"
    application
}

application {
    mainClass.set("esq.dam.MainKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

group = "esq.dam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.17.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")

    // Para SQLite
    implementation("org.xerial:sqlite-jdbc:3.43.0.0")

    // Para PostgreSQL (se usará opcionalmente más adelante)
    implementation("org.postgresql:postgresql:42.7.1")

    // Para MySQL (coordenadas nuevas desde 8.0.31)
    implementation("com.mysql:mysql-connector-j:8.4.0")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}