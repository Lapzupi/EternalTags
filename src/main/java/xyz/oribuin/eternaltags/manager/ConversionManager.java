package xyz.oribuin.eternaltags.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternaltags.obj.Tag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class ConversionManager extends Manager {

    private FileConfiguration oldConfig;
    private File configFile;
    private Map<String, Tag> loadedTags;
    private Map<String, Object> configOptions;

    public ConversionManager(RosePlugin rosePlugin) {
        super(rosePlugin);

    }

    public void reload() {
        if (!this.shouldConvert()) {
            return;
        }

        this.rosePlugin.getLogger().warning("Converting old EternalTags configs");
        this.configFile = new File(this.rosePlugin.getDataFolder(), "config.yml");

        this.oldConfig = YamlConfiguration.loadConfiguration(this.configFile);
        this.loadedTags = new HashMap<>();
        this.configOptions = new HashMap<>();
        this.loadConfiguration();

        // Delete the folder.
        try {
            Files.delete(this.configFile.toPath());
        } catch (IOException e) {
            rosePlugin.getLogger().severe(e::toString);
        }

        final ConfigurationManager manager = this.rosePlugin.getManager(ConfigurationManager.class);
        manager.reload();
        this.configFile = new File(this.rosePlugin.getDataFolder(), "tags.yml");
        final CommentedFileConfiguration newConfig = manager.getConfig();

        this.configOptions.forEach(newConfig::set);
        newConfig.save(this.configFile);

    }

    @Override
    public void disable() {
        //nothing
    }

    private void loadConfiguration() {
        // Load all the old options, so we're not fucking people over.
        final CommentedFileConfiguration newConfig = this.rosePlugin.getManager(ConfigurationManager.class).getConfig();

        // load all the old config options.
        for (String path : this.oldConfig.getKeys(false)) {
            final String remappedPath = this.getRemappedOptions().get(path);
            if (remappedPath != null)
                this.configOptions.put(remappedPath, this.oldConfig.get(path));
        }

        // Transfer over mysql settings.
        final ConfigurationSection mysqlSection = this.oldConfig.getConfigurationSection("mysql");
        if (mysqlSection != null) {
            for (String path : mysqlSection.getKeys(false)) {
                final String remappedPath = this.getRemappedOptions().get("mysql." + path);
                if (remappedPath != null) {
                    this.configOptions.put(remappedPath, mysqlSection.get(path));
                }
            }
        }

        this.getRemappedOptions().forEach((s, s2) -> newConfig.set(s2, this.configOptions.get(s)));
        newConfig.save(this.configFile);

        // Convert tags.
        final ConfigurationSection section = this.oldConfig.getConfigurationSection("tags");
        if (section == null)
            return;

        this.rosePlugin.getLogger().warning("Converting Tags into tags.yml");
        for (String key : section.getKeys(false)) {
            String name = section.getString(key + ".name", key);
            String tagText = section.getString(key + ".tag");

            if (name == null || tagText == null)
                continue;

            Tag tag = new Tag(key, name, tagText);
            List<String> description = section.get(key + ".description") instanceof String
                    ? Collections.singletonList(section.getString(key + ".description"))
                    : section.getStringList(key + ".description");

            tag.setDescription(description);

            String permission = section.getString(key + ".permission");
            if (permission != null)
                tag.setPermission(permission);

            int order = section.getInt(key + ".order");
            if (order != 0 && order != -1)
                tag.setOrder(order);

            Material icon = Material.matchMaterial(section.getString(key + ".icon", ""));
            if (icon != null)
                tag.setIcon(new ItemStack(icon));

            this.loadedTags.put(key, tag);
        }

        final TagsManager manager = this.rosePlugin.getManager(TagsManager.class);
        manager.wipeTags();
        manager.saveTags(this.loadedTags);
    }

    public Map<String, String> getRemappedOptions() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("default-tag", "default-tag");
        map.put("formatted_placeholder", "formatted-placeholder");
        map.put("remove-inaccessible-tags", "remove-inaccessible-tags");
        map.put("mysql.enabled", "mysql-settings.enabled");
        map.put("mysql.host", "mysql-settings.hostname");
        map.put("mysql.port", "mysql-settings.port");
        map.put("mysql.dbname", "mysql-settings.database-name");
        map.put("mysql.username", "mysql-settings.user-name");
        map.put("mysql.password", "mysql-settings.user-password");
        map.put("mysql.ssl", "mysql-settings.use-ssl");
        return map;
    }

    /**
     * Check if the plugin needs to convert into v1.1.0
     *
     * @return If the plugin has converted or not.
     */
    public boolean shouldConvert() {
        final File file = new File(this.rosePlugin.getDataFolder(), "tags.yml");

        return !file.exists() && this.rosePlugin.getConfig().get("tags") != null;
    }

}
