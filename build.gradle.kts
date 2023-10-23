plugins {
    id("java")
}

group = "reverse.retrofit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("io.github.skylot:jadx-core:1.4.7")
    implementation("ch.qos.logback:logback-classic:1.4.11")

    runtimeOnly("io.github.skylot:jadx-dex-input:1.4.7")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}