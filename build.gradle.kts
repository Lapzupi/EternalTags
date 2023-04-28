plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

group = "xyz.oribuin"
version = "1.2.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.mattstudios.me/artifactory/public/")
    
}

dependencies {
    // General Dependencies
    compileOnly(libs.paper.api)
    compileOnly(libs.annotations)
    
    library(libs.adventure.api)
    library(libs.adventure.legacy)
    library(libs.adventure.gson)
    
    // Plugins
    compileOnly(libs.vault.api)
    compileOnly(libs.placeholder.api)
    compileOnly(libs.headdatabase)
    compileOnly(libs.oraxen)
    compileOnly(libs.itemsadder.api)
    
    // Mojang dependencies
    compileOnly(libs.mojang.authlib)
    
    // Frameworks & APIs
    api(libs.rosegarden)
    api(libs.triumph.gui)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        minimize()
        
        archiveClassifier.set("")
        dependencies {
            exclude("com.google.code.gson:gson")
            exclude("net.kyori:*")
        }
        relocate("dev.rosewood.rosegarden", "xyz.oribuin.eternaltags.libs.rosegarden")
        relocate("dev.triumphteam.gui", "xyz.oribuin..eternaltags.libs.gui")
    }
    
    compileJava {
        options.compilerArgs.add("-parameters")
        options.isFork = true
        options.encoding = "UTF-8"
    }
}

bukkit {
    name = "EternalTags"
    main = "xyz.oribuin.eternaltags.EternalTags"
    version = rootProject.version.toString()
    authors = listOf("Oribuin", "sarhatabaot")
    apiVersion = "1.19"
    description = "A simple tag plugin alternative to other Tag plugins (With Hex Support)"
    website = "https://www.spigotmc.org/resources/eternaltags.91842/" //https://github.com/Lapzupi/EternalTags
    softDepend = listOf("Vault", "PlaceholderAPI", "Oraxen", "ItemsAdder", "HeadDatabase")
}



