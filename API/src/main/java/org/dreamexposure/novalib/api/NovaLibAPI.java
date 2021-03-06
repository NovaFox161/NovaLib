package org.dreamexposure.novalib.api;

import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.dreamexposure.novalib.api.bukkit.file.CustomConfig;
import org.dreamexposure.novalib.api.bukkit.packets.PacketManager;
import org.dreamexposure.novalib.api.network.crosstalk.client.ClientSocketHandler;
import org.dreamexposure.novalib.api.network.crosstalk.server.ServerSocketHandler;
import org.dreamexposure.novalib.api.network.pubsub.PubSubManager;

import java.util.LinkedHashMap;
import java.util.UUID;

@SuppressWarnings({"unused", "ControlFlowStatementWithoutBraces", "Duplicates"})
public class NovaLibAPI {
    private static NovaLibAPI instance;

    private JavaPlugin bukkitPlugin;
    private Plugin bungeePlugin;


    private CustomConfig bukkitConfig;
    private org.dreamexposure.novalib.api.bungee.file.CustomConfig bungeeConfig;

    private boolean bukkit = false;

    private NovaLibAPI() {
    } //Prevent initialization

    /**
     * Gets the current loaded instance of the API.
     *
     * @return The  current loaded instance of the API.
     */
    public static NovaLibAPI getApi() {
        if (instance == null)
            instance = new NovaLibAPI();
        return instance;
    }

    /**
     * Initializes all parts of the API. This is automatically handled on server boot and SHOULD NOT be called by any plugins.
     */
    public void initAPIForBukkit(JavaPlugin _plugin) {
        bukkitPlugin = _plugin;
        bungeePlugin = null;
        bukkit = true;

        PacketManager.getManager().init(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);

        bukkitConfig = new CustomConfig(bukkitPlugin, "", "config.yml");

        bukkitConfig.update(getSettings(false));

        if (debug())
            getBukkitPlugin().getLogger().info("Started NovaLibAPI!");

        //Start CrossTalk
        if (bukkitConfig.get().getBoolean("CrossTalk.Enabled")) {
            if (bukkitConfig.get().getBoolean("CrossTalk.Self as Server"))
                ServerSocketHandler.initListener();
            else
                ClientSocketHandler.initListener();
        }
    
        //Start SubPub System
        if (bukkitConfig.get().getBoolean("Redis.PubSub.Enabled")) {
            PubSubManager.get().init();
        }
    }

    /**
     * Initializes all parts of the API. This is automatically handled on server boot and SHOULD NEVER be called by any plugins.
     */
    public void initAPIForBungee(Plugin _plugin) {
        bungeePlugin = _plugin;
        bukkitPlugin = null;
        bukkit = false;

        bungeeConfig = new org.dreamexposure.novalib.api.bungee.file.CustomConfig(bungeePlugin, "", "config.yml");
        bungeeConfig.update(getSettings(true));

        if (debug())
            getBungeePlugin().getLogger().info("Started NovaLibAPI!");

        //Start CrossTalk
        if (bungeeConfig.get().getBoolean("CrossTalk.Enabled")) {
            if (bungeeConfig.get().getBoolean("CrossTalk.Self as Server"))
                ServerSocketHandler.initListener();
            else
                ClientSocketHandler.initListener();
        }
    
        //Start SubPub System
        if (bungeeConfig.get().getBoolean("Redis.SubPub.Enabled")) {
            PubSubManager.get().init();
        }
        
    }

    /**
     * Shuts down the API gracefully. This is automatically handled on server shutdown and SHOULD NOT be called by any plugins.
     */
    public void shutdownAPI() {
        if (bukkit && getBukkitConfig().get().getBoolean("CrossTalk.Enabled")) {
            if (getBukkitConfig().get().getBoolean("CrossTalk.Self as Server"))
                ServerSocketHandler.shutdownListener();
            else
                ClientSocketHandler.shutdownListener();
        } else if (!bukkit && getBungeeConfig().get().getBoolean("CrossTalk.Enabled")) {
            if (getBungeeConfig().get().getBoolean("CrossTalk.Self as Server"))
                ServerSocketHandler.shutdownListener();
            else
                ClientSocketHandler.shutdownListener();
        }
    }

    /**
     * Hook a Bukkit plugin into NovaLibAPI. This will be needed more in the future to handle certain functions!
     *
     * @param _bPlugin The plugin to hook.
     */
    public void hookBukkitPlugin(JavaPlugin _bPlugin) {
        bukkitPlugin.getLogger().info("Plugin hooked: " + _bPlugin.getDescription().getName());
    }

    /**
     * Hook a Bungee plugin into NovaLibAPI. This will be needed more in the future to handle certain functions.
     *
     * @param _bPlugin The plugin to hook.
     */
    public void hookBungeePlugin(Plugin _bPlugin) {
        bungeePlugin.getLogger().info("Plugin hooked: " + _bPlugin.getDescription().getName());
    }

    private LinkedHashMap<String, Object> getSettings(boolean bungee) {
        LinkedHashMap<String, Object> s = new LinkedHashMap<>();

        s.put("DO NOT DELETE", "NovaLib and NovaLibAPI are developed and managed by DreamExposure");
        s.put("Updates.Check", true);
        s.put("Updates.Download", false);

        s.put("Console.Debug", false);
        s.put("Console.Verbose", false);

        s.put("Stats.Enabled", true);
        s.put("Stats.Server-Id", UUID.randomUUID().toString());
        if (bungee)
            s.put("Stats.Network-Id", UUID.randomUUID().toString());
        else
            s.put("Stats.Network-Id", "GET_FROM_BUNGEE_IF_IN_NETWORK");
    
        s.put("Redis.PubSub.Enabled", false);
        s.put("Redis.PubSub.Hostname", "localhost");
        s.put("Redis.PubSub.Port", 6379);
        s.put("Redis.PubSub.Password", "password");
        
        s.put("CrossTalk.Enabled", false);
        if (bungee)
            s.put("CrossTalk.Self as Server", true);
        else
            s.put("CrossTalk.Self as Server", false);
        s.put("CrossTalk.Server.Hostname", "localhost");
        s.put("CrossTalk.Server.Port", 5200);
        s.put("CrossTalk.Client.Hostname", "localhost");
        s.put("CrossTalk.Client.Port", 5301);
    
        s.put("Network.ServerName", "SERVER NAME AS MATCHES IN BUNGEE CONFIG!");

        return s;
    }


    /**
     * Gets the CustomConfig for when the API is running on a Bukkit Server.
     *
     * @return The CustomConfig for when the API is running on a Bukkit Server.
     */
    public CustomConfig getBukkitConfig() {
        return bukkitConfig;
    }

    /**
     * Gets the CustomConfig for when the API is running on a Bungee Proxy Server.
     *
     * @return The CustomConfig for when the API is running on a Bungee Proxy Server.
     */
    public org.dreamexposure.novalib.api.bungee.file.CustomConfig getBungeeConfig() {
        return bungeeConfig;
    }

    /**
     * Gets the BukkitPlugin for when the API is running on a Bukkit Server.
     *
     * @return The BukkitPlugin for when the API is running on a Bukkit Server.
     */
    public JavaPlugin getBukkitPlugin() {
        return bukkitPlugin;
    }

    /**
     * Gets the BungeePlugin for when the API is running on a Bungee Proxy Server.
     *
     * @return The BungeePlugin for when the API is running on a Bungee Proxy Server.
     */
    public Plugin getBungeePlugin() {
        return bungeePlugin;
    }

    /**
     * Gets whether or not the server is Bukkit or Bungee. This is only for internal stuff for handling bungee/bukkit configs within the API.
     *
     * @return Whether or not hte server is Bukkit or Bungee.
     */
    public boolean isBukkit() {
        return bukkit;
    }

    /**
     * Gets whether or not NovaLib is in Debug Mode.
     *
     * @return Whether or not NovaLib is in Debug Mode.
     */
    public boolean debug() {
        if (getApi().isBukkit())
            return getApi().getBukkitConfig().get().getBoolean("Console.Debug");
        else
            return getApi().getBungeeConfig().get().getBoolean("Console.Debug");
    }

    /**
     * Gets whether or not NovaLib is in Verbose Mode.
     *
     * @return Whether or not NovaLib is in Verbose Mode.
     */
    public boolean verbose() {
        if (getApi().isBukkit())
            return getApi().getBukkitConfig().get().getBoolean("Console.Verbose");
        else
            return getApi().getBungeeConfig().get().getBoolean("Console.Verbose");
    }
    
    public String getServerName() {
        if (getApi().isBukkit())
            return getApi().getBukkitConfig().get().getString("Network.ServerName");
        else
            return getApi().getBungeeConfig().get().getString("Network.ServerName");
    }
}