rootProject.name = "EternalTags"

dependencyResolutionManagement {
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
    versionCatalogs {
        create("libs") {
            library("paper-api", "io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
            library("annotations", "org.jetbrains:annotations:23.0.0")
            
            version("adventure", "4.17.0")
            library("adventure-api", "net.kyori","adventure-api").versionRef("adventure")
            library("adventure-legacy", "net.kyori","adventure-text-serializer-legacy").versionRef("adventure")
            library("adventure-gson", "net.kyori","adventure-text-serializer-gson").versionRef("adventure")
            bundle("adventure", listOf("adventure-api", "adventure-legacy", "adventure-gson"))
            
            library("itemsadder-api", "com.github.LoneDev6:api-itemsadder:3.2.5")
            
            library("rosegarden", "dev.rosewood:rosegarden:1.2.1")
            
            library("vault-api", "com.github.MilkBowl:VaultAPI:1.7.1")
            
            library("placeholder-api", "me.clip:placeholderapi:2.11.6")
            library("headdatabase", "com.arcaniax:HeadDatabase-API:1.3.1")
            library("mojang-authlib", "com.mojang:authlib:3.17.30")

            plugin("shadow", "com.gradleup.shadow").version("9.0.0-beta4")
            plugin("bukkit-yml", "net.minecrell.plugin-yml.bukkit").version("0.6.0")
            
            library("triumph-gui", "dev.triumphteam:triumph-gui:3.1.4")
        }
    }
}