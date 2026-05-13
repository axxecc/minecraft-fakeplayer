plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}



repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}


dependencies {
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")

    compileOnly(project(":fakeplayer-api"))

    implementation("dev.jorel:commandapi-paper-shade:11.2.0")
    compileOnly("com.mojang:authlib:4.0.43")
    compileOnly("com.mojang:brigadier:1.1.8")
    compileOnly("io.netty:netty-transport:4.1.82.Final")
    compileOnly("com.github.Jikoo:OpenInv:5.1.13")
    compileOnly("me.clip:placeholderapi:2.11.6")

    implementation("com.github.tanyaofei.devtools:devtools-core:0.1.7-SNAPSHOT")
    implementation("com.github.tanyaofei.devtools:devtools-command:0.1.7-SNAPSHOT")
    implementation("com.github.tanyaofei.devtools:devtools-database:0.1.7-SNAPSHOT")
}
