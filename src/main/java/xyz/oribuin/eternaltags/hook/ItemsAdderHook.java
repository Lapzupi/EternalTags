package xyz.oribuin.eternaltags.hook;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;


/**
 * @author sarhatabaot
 */
public final class ItemsAdderHook {
    private ItemsAdderHook(){
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param item The tag to parse
     * @return The parsed glyph
     */
    public static ItemStack parseItem(String item) {
        CustomStack customStack = CustomStack.getInstance(item);
        if(customStack == null)
            return null;
        return customStack.getItemStack();
    }

    /**
     * @return If itemsadder is enabled or not
     */
    public static boolean enabled() {
        return Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
    }
}
