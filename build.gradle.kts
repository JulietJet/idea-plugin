import org.jetbrains.intellij.IntelliJPluginExtension
import org.jetbrains.intellij.tasks.PatchPluginXmlTask

buildscript {
    repositories {
        maven(url = "https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("gradle.plugin.org.jetbrains.intellij.plugins:gradle-intellij-plugin:0.3.7")
    }
}

description = "Idea Plugin Sample"

tasks.existing(Wrapper::class) {
    gradleVersion = "4.10.2"
    distributionType = Wrapper.DistributionType.ALL
}

plugins {
    id("java")
    id("org.jetbrains.intellij") version ("0.4.8")
}

tasks.withType(type = JavaCompile::class) {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compile ("org.apache.clerezza.ext:org.json.simple:0.4")
}

configure<IntelliJPluginExtension> {
    version = "2019.1.2"
}

val fatJar = task("fatJar", type = Jar::class) {
    from(configurations.compile.map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks["jar"] as CopySpec)
}

tasks.withType(PatchPluginXmlTask::class) {
    changeNotes("Add change notes here.<br><em>most HTML tags may be used</em>")
}


tasks {
    "buildPlugin" {
        dependsOn(fatJar)
    }
}
