package xyz.oribuin.eternaltags;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import xyz.oribuin.eternaltags.gui.MenuProvider;
import xyz.oribuin.eternaltags.hook.Expansion;
import xyz.oribuin.eternaltags.hook.ItemsAdderHook;
import xyz.oribuin.eternaltags.listener.BungeeListener;
import xyz.oribuin.eternaltags.listener.PlayerListeners;
import xyz.oribuin.eternaltags.manager.CommandManager;
import xyz.oribuin.eternaltags.manager.ConfigurationManager;
import xyz.oribuin.eternaltags.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternaltags.manager.ConversionManager;
import xyz.oribuin.eternaltags.manager.DataManager;
import xyz.oribuin.eternaltags.manager.LocaleManager;
import xyz.oribuin.eternaltags.manager.PluginConversionManager;
import xyz.oribuin.eternaltags.manager.TagsManager;
import xyz.oribuin.eternaltags.util.EventWaiter;

import java.util.Arrays;
import java.util.List;

public class EternalTags extends RosePlugin {

    private static EternalTags instance;
    private static EventWaiter eventWaiter;

    public static EternalTags getInstance() {
        return instance;
    }

    public EternalTags() {
        super(91842, 11508, ConfigurationManager.class, DataManager.class, LocaleManager.class, CommandManager.class);
        instance = this;
    }

    @Override
    public void enable() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        // Make sure the server has PlaceholderAPI
        if (!pluginManager.isPluginEnabled("PlaceholderAPI")) {
            this.getLogger().severe("Please install PlaceholderAPI onto your server to use this plugin.");
            pluginManager.disablePlugin(this);
            return;
        }

        // Make sure the server is on MC 1.16
        if (NMSUtil.getVersionNumber() < 16) {
            this.getLogger().severe("This plugin only supports 1.16+ Minecraft.");
            pluginManager.disablePlugin(this);
            return;
        }

        // Register Plugin Listeners
        pluginManager.registerEvents(new PlayerListeners(), this);
        pluginManager.registerEvents(new Listener() {
            @EventHandler
            public void onItemsAdderLoad(ItemsAdderLoadDataEvent event) {
                getLogger().info("Detected ItemsAdder load event...");
                getLogger().info("Reloading EternalTags");
                reload();
            }
        }, this);


        // Register Plugin Messaging Channels
        if (Setting.PLUGIN_MESSAGING.getBoolean()) {
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener(this));
        }

        // Register Event Waiter
        eventWaiter = new EventWaiter();

        // Register PlaceholderAPI Expansion
        new Expansion(this).register();
    }

    @Override
    public void reload() {
        super.reload(); // Reload the managers

        MenuProvider.reload(); // Reload the menu provider
    }

    @Override
    public void disable() {
        if (Setting.PLUGIN_MESSAGING.getBoolean()) {
            this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
            this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        }
    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return Arrays.asList(
                ConversionManager.class,
                TagsManager.class,
                PluginConversionManager.class
        );
    }

    public static EventWaiter getEventWaiter() {
        return eventWaiter;
    }

}
