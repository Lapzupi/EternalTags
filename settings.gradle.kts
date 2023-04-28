rootProject.name = "EternalTags"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("paper-api","io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
            library("annotations", "org.jetbrains:annotations:23.0.0")
            library("oraxen", "com.github.oraxen:oraxen:afc4903680")
            
            version("adventure", "4.11.0")
            library("adventure-api", "net.kyori","adventure-api").versionRef("adventure")
            library("adventure-legacy", "net.kyori","adventure-text-serializer-legacy").versionRef("adventure")
            library("adventure-gson", "net.kyori","adventure-text-serializer-gson").versionRef("adventure")
            
            library("itemsadder-api", "com.github.LoneDev6:api-itemsadder:3.2.5")
            
            library("rosegarden", "dev.rosewood:rosegarden:1.2.1")
            
            library("vault-api", "com.github.MilkBowl:VaultAPI:1.7.1")
            
            library("placeholder-api", "me.clip:placeholderapi:2.11.3")
            library("headdatabase", "com.arcaniax:HeadDatabase-API:1.3.1")
            library("mojang-authlib", "com.mojang:authlib:3.17.30")
            
            library("triumph-gui", "dev.triumphteam:triumph-gui:3.1.4")
        }
    }
}