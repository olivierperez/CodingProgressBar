plugins {
    id("org.jetbrains.intellij") version "1.0"
    kotlin("jvm") version "1.4.31"
    java
}

group = "fr.o80"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2021.2")
}
tasks {
    patchPluginXml {
        changeNotes.set("""
            Add change notes here.<br>
            <em>most HTML tags may be used</em>        """.trimIndent())
        sinceBuild.set("192")
        untilBuild.set("213.*")
    }
}
tasks.getByName<Test>("test") {
    useJUnitPlatform()
}