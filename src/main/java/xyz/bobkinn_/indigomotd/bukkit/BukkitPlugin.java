package xyz.bobkinn_.indigomotd.bukkit;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.bobkinn_.indigomotd.ConfigAdapter;
import xyz.bobkinn_.indigomotd.Platform;

import java.util.logging.Logger;

public final class BukkitPlugin extends JavaPlugin {
    public static JavaPlugin plugin;
    public static Logger logger;
    @Override
    public void onEnable() {
        plugin=this;
        logger=plugin.getLogger();
        Platform.currentPlatform=Platform.BUKKIT;
        reload();
        PingListener listener = new PingListener();
        listener.reload();
        getServer().getPluginManager().registerEvents(listener,this);
        new Metrics(this,16117);
    }

    public static void reload(boolean onStart){
        if (onStart) logger.info("Loading config..");
        ConfigAdapter.reload(plugin.getDataFolder(),plugin.getResource("config.yml"),logger);
    }

    public void reload() {
        reload(true);
    }

    @Override
    public void onDisable() {

    }
}
