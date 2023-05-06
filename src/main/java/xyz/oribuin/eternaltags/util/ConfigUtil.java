package xyz.oribuin.eternaltags.util;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author sarhatabaot
 */
public class ConfigUtil {
    private ConfigUtil() {
        throw new UnsupportedOperationException();
    }
    
    public static @Nullable String getString(final @NotNull CommentedConfigurationSection config, final Player player, final String path, final String defaultValue) {
        final String value = config.getString(path, defaultValue);
        if (value == null)
            return null;
        return PlaceholderAPI.setPlaceholders(player, config.getString(path, defaultValue));
    }
}
