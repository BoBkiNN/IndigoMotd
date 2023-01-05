package xyz.bobkinn_.indigomotd;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.bstats.bungeecord.Metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

public final class IndigoMOTD extends Plugin {
    public static Plugin plugin;
    public static Configuration cfg;
    public static Logger logger;
    public static PluginListener listener;

    @Override
    public void onEnable() {
        Platform.currentPlatform=Platform.BUNGEE;
        // Plugin startup logic
        plugin=this;
        logger=this.getLogger();
        reloadCfg();
        listener = new PluginListener();
        listener.reload();
        int bStatsBungeeId = 16082;
        // Metrics metricsBungee = TODO add custom charts
        new Metrics(this,bStatsBungeeId);

        getProxy().getPluginManager().registerListener(this,listener);
        getProxy().getPluginManager().registerListener(this,new ReloadListener());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void reloadCfg(){
        logger.info("Loading config..");
        ConfigAdapter.reload(plugin.getDataFolder(), plugin.getResourceAsStream("config.yml"),logger);
        cfg = ConfigAdapter.cfg;
        if (BaseUtils.linesEmpty()){
            ArrayList<String> l1 = new ArrayList<>(IndigoMOTD.cfg.getStringList("motd.lineOne"));
            ArrayList<String> l2 = new ArrayList<>(IndigoMOTD.cfg.getStringList("motd.lineTwo"));
            if (l1.isEmpty() && l2.isEmpty()){
                cfg.set("motd.lineOne", Collections.singletonList("Line 1"));
                cfg.set("motd.lineOne", Collections.singletonList("Line 2"));
            } else if (l1.isEmpty()){
                cfg.set("motd.lineOne", Collections.singletonList("Line 1"));
            } else if (l2.isEmpty()){
                cfg.set("motd.lineOne", Collections.singletonList("Line 2"));
            }
        }
        if (!BaseUtils.linesCountSame()){
            logger.severe("Lines count are not same, lineTwo will be similar as lineOne");
            cfg.set("motd.lineTwo",cfg.getStringList("motd.lineOne"));
        }
        if (listener != null) listener.reload();
        logger.info("Config reloaded");
    }
}
