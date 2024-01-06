import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.busitay"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven( "https://jitpack.io")
}

dependencies {
    implementation(compose.desktop.currentOs)

    val ktorVersion = "2.3.7"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    val exposedVersion = "0.45.0"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    val h2Version= "2.2.224"
    implementation("com.h2database:h2:$h2Version")

    val javaStreamPlayerVersion = "9.0.4"
    implementation ("com.github.goxr3plus:java-stream-player:$javaStreamPlayerVersion")

    val mp3agicVersion = "0.9.1"
    implementation ("com.mpatric:mp3agic:$mp3agicVersion")

    val junitVersion = "4.13.2"
    testImplementation("junit:junit:$junitVersion")

}

kotlin {
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }


}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Musync"
            packageVersion = "1.0.0"
            outputBaseDir.set(project.buildDir.resolve("exports"))
            modules("java.instrument", "java.management", "java.prefs", "java.sql", "jdk.unsupported")
            jvmArgs(
                "-Dapple.awt.application.appearance=system"
            )
        }


    }
}
