plugins {
    id("java")
    id("com.gradleup.shadow") version "9.1.0"
}

group = "io.github.hello09x.fakeplayer"
version = rootProject.version

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

dependencies {
    implementation(project(":fakeplayer-core"))
    implementation(project(":fakeplayer-api"))
    implementation(project(":fakeplayer-v1_21_8"))
    implementation(project(":fakeplayer-v1_21_11"))
    implementation(project(":fakeplayer-v26_1"))
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveFileName.set("fakeplayer-${project.version}.jar")
    archiveBaseName.set("fakeplayer")
    archiveClassifier.set("")
    archiveVersion.set("")

    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
}

tasks.jar {
    enabled = false
    dependsOn(tasks.shadowJar)
}

tasks.register("copyToServers") {
    dependsOn(tasks.shadowJar)

    doLast {
        val jarFile = tasks.shadowJar.get().archiveFile.get().asFile
        val servers = listOf(
                "../server-1.21.8/plugins"
        )

        servers.forEach { serverDir ->
            val dir = file(serverDir)
            if (!dir.exists()) {
                dir.mkdirs()
                println("Created directory: $serverDir")
            }

            copy {
                from(jarFile)
                into(dir)
                rename { "fakeplayer.jar" }
            }
            println("Copied to: $serverDir/fakeplayer.jar")
        }
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
    finalizedBy(tasks.named("copyToServers"))
}