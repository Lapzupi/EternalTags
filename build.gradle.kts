plugins {
    `java-library`
    alias(libs.plugins.shadow)
    alias(libs.plugins.bukkit.yml)
}

group = "xyz.oribuin"
version = "1.2.2-Lapzupi"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    // General Dependencies
    compileOnly(libs.paper.api)
    compileOnly(libs.annotations)
    // Plugins
    compileOnly(libs.vault.api)
    compileOnly(libs.placeholder.api)
    compileOnly(libs.headdatabase)
    compileOnly(libs.itemsadder.api)
    // Mojang dependencies
    compileOnly(libs.mojang.authlib)
    
    // Frameworks & APIs
    api(libs.rosegarden)
    api(libs.triumph.gui)

    library(libs.bundles.adventure)
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
        relocate("dev.triumphteam.gui", "xyz.oribuin.eternaltags.libs.gui")

        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
    }
    
    compileJava {
        options.compilerArgs.add("-parameters")
        options.isFork = true
        options.encoding = "UTF-8"
    }
}

bukkit {
    name = "LapzupiEternalTags"
    main = "xyz.oribuin.eternaltags.EternalTags"
    version = rootProject.version.toString()
    authors = listOf("Oribuin", "sarhatabaot")
    apiVersion = "1.21"
    description = "A simple tag plugin alternative to other Tag plugins (With Hex Support)"
    website = "https://www.spigotmc.org/resources/eternaltags.91842/" //https://github.com/Lapzupi/EternalTags
    depend = listOf("PlaceholderAPI")
    softDepend = listOf("Vault", "PlaceholderAPI", "ItemsAdder", "HeadDatabase")
}



