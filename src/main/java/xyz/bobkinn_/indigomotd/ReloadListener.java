package xyz.bobkinn_.indigomotd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ReloadListener implements Listener {
    @EventHandler
    public void onReload(ProxyReloadEvent e){
        IndigoMOTD.logger.info(ChatColor.GREEN +"Reloading..");
        IndigoMOTD.reloadCfg();
    }
}
