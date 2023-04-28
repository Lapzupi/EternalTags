package xyz.oribuin.eternaltags.util;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 * @author sarhatabaot
 */
public class ConfigUtil {
    private ConfigUtil() {
        throw new UnsupportedOperationException();
    }
    
    public static String getString(final CommentedConfigurationSection config, final Player player, final String path, final String defaultValue) {
        return PlaceholderAPI.setPlaceholders(player, config.getString(path, defaultValue));
    }
}
