package xyz.oribuin.eternaltags.util;

import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternaltags.hook.ItemsAdderHook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public final class TagsUtils {

    private TagsUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Convert a location to the center of the block
     *
     * @param location The location to convert
     * @return The center of the block
     */
    public static Location center(Location location) {
        final Location loc = location.getBlock().getLocation().clone();
        loc.add(0.5, 0.5, 0.5);
        loc.setYaw(180f);
        loc.setPitch(0f);
        return loc;
    }

    /**
     * Get a bukkit color from a hex code
     *
     * @param hex The hex code
     * @return The bukkit color
     */
    public static Color fromHex(String hex) {
        if (hex == null)
            return Color.BLACK;

        java.awt.Color awtColor;
        try {
            awtColor = java.awt.Color.decode(hex);
        } catch (NumberFormatException e) {
            return Color.BLACK;
        }

        return Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
    }

    /**
     * Get a configuration value or default from the file config
     *
     * @param config The configuration file.
     * @param path   The path to the value
     * @param def    The default value if the original value doesnt exist
     * @return The config value or default value.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(FileConfiguration config, String path, T def) {
        return config.get(path) != null ? (T) config.get(path) : def;
    }

    /**
     * Get a value from a configuration section.
     *
     * @param section The configuration section
     * @param path    The path to the option.
     * @param def     The default value for the option.
     * @return The config option or the default.
     */
    public static <T> T get(ConfigurationSection section, String path, T def) {
        return section.get(path) != null ? (T) section.get(path) : def;
    }

    /**
     * Get the total number of spare slots in a player's inventory
     *
     * @param player The player
     * @return The amount of empty slots.
     */
    public static int getSpareSlots(Player player) {
        final List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < 36; i++)
            slots.add(i);

        return (int) slots.stream().map(integer -> player.getInventory().getItem(integer))
                .filter(itemStack -> itemStack == null || itemStack.getType() == Material.AIR)
                .count();
    }

    /**
     * Gets a location as a string key
     *
     * @param location The location
     * @return the location as a string key
     * @author Esophose
     */
    public static String locationAsKey(Location location) {
        return String.format("%s;%.2f;%.2f;%.2f", location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    /**
     * Get a location from a string key
     *
     * @param key The key
     * @return The location
     */
    public static Location locationFromKey(String key) {
        if (key == null || key.isEmpty())
            return null;

        // format is world;x;y;z
        final String[] split = key.split(";");
        if (split.length != 4)
            return null;

        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
    }

    /**
     * Format a material name through this long method
     *
     * @param material The material
     * @return The material name.
     */
    public static String format(Material material) {
        return WordUtils.capitalizeFully(material.name().toLowerCase().replace("_", " "));
    }


    private static ItemStack getBaseItem(ConfigurationSection config, String path) {
        final String materialString = config.getString(path + ".material", "STONE");
        if (materialString.contains("itemsadder:")) {
            return ItemsAdderHook.parseItem(config.getString(path + ".material", "").replace("itemsadder:", ""));
        }
        Material material = Material.getMaterial(get(config, path + ".material", "STONE"));
        return new ItemStack(Objects.requireNonNullElse(material, Material.STONE));
    }

    /**
     * Get ItemStack from CommentedFileSection path
     *
     * @param config       The CommentedFileSection
     * @param path         The path to the item
     * @param player       The player
     * @param placeholders The placeholders
     * @return The itemstack
     */
    public static ItemStack getItemStack(ConfigurationSection config, String path, Player player, StringPlaceholders placeholders) {
        ItemStack baseItem = getBaseItem(config, path);

        // Format the item lore
        List<String> lore = get(config, path + ".lore", List.of());
        lore = lore.stream().map(s -> format(player, s, placeholders)).collect(Collectors.toList());

        // Get item flags
        ItemFlag[] flags = get(config, path + ".flags", new ArrayList<String>())
                .stream()
                .map(String::toUpperCase)
                .map(ItemFlag::valueOf)
                .toArray(ItemFlag[]::new);

        // Build the item stack
        ItemBuilder builder = new ItemBuilder(baseItem)
                .setName(format(player, get(config, path + ".name", null), placeholders))
                .setLore(lore)
                .setAmount(Math.max(get(config, path + ".amount", 1), 1))
                .setFlags(flags)
                .glow(get(config, path + ".glow", false))
                .setTexture(get(config, path + ".texture", null))
                .setPotionColor(fromHex(get(config, path + ".potion-color", null)));

        //builder.setModel(get(config, path + ".model-data", -1)); TODO TEMP FIX FOR IA SUPPORT

        // Get item owner
        String owner = get(config, path + ".owner", null);
        if (owner != null)
            builder.setOwner(Bukkit.getOfflinePlayer(UUID.fromString(owner)));

        // Get item enchantments
        final ConfigurationSection enchants = config.getConfigurationSection(path + ".enchants");
        if (enchants != null) {
            enchants.getKeys(false).forEach(key -> {
                Enchantment enchant = Arrays.stream(Enchantment.values()).filter(e -> e.getKey().getKey().equalsIgnoreCase(key)).findFirst().orElse(null);

                if (enchant == null)
                    return;

                builder.addEnchant(enchant, enchants.getInt(key));
            });
        }

        return builder.create();
    }

    /**
     * Get ItemStack from CommentedFileSection path
     *
     * @param config The CommentedFileSection
     * @param path   The path to the item
     * @return The itemstack
     */
    public static ItemStack getItemStack(ConfigurationSection config, String path) {
        return getItemStack(config, path, null, StringPlaceholders.empty());
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player The player to format the string for
     * @param text   The string to format
     * @return The formatted string
     */
    public static String format(Player player, String text) {
        return format(player, text, StringPlaceholders.empty());
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player       The player to format the string for
     * @param text         The text to format
     * @param placeholders The placeholders to replace
     * @return The formatted string
     */
    public static String format(Player player, String text, StringPlaceholders placeholders) {
        return HexUtils.colorify(PlaceholderAPI.setPlaceholders(player, placeholders.apply(text)));
    }

    /**
     * Format a List<String> into a single String
     *
     * @param list The list
     * @return the new formatted string.
     */
    public static String formatList(List<String> list) {
        return String.join(", ", list);
    }


    /**
     * Format a List<String> into a single String
     *
     * @param list The list
     * @return the new formatted string.
     */
    public static String formatList(String[] list) {
        return formatList(Arrays.asList(list));
    }

}
