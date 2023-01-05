package xyz.bobkinn_.indigomotd.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.util.CachedServerIcon;
import xyz.bobkinn_.indigomotd.ConfigAdapter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
    public static CachedServerIcon getRandomIcon(List<CachedServerIcon> icons){
        int r = new Random().nextInt(icons.size());
        return icons.get(r);
    }

    public static CachedServerIcon getDefaultIcon(){
        return BukkitPlugin.plugin.getServer().getServerIcon();
    }

    public static String color(String text){
        return ChatColor.translateAlternateColorCodes('&',text);
    }

    public static ArrayList<CachedServerIcon> loadIcons(){
        ArrayList<CachedServerIcon> icons = new ArrayList<>();
        BukkitPlugin.logger.info("Loading icons..");
        File iconFolder = new File(BukkitPlugin.plugin.getDataFolder(),"icons");
        if (!iconFolder.exists()){
            if(!iconFolder.mkdirs()){
                icons.add(getDefaultIcon());
                return icons;
            }
        }
        if (!iconFolder.isDirectory()){
            BukkitPlugin.logger.severe("\"icons\" is not directory, failed to load icons");
            icons.add(getDefaultIcon());
            return icons;
        }
        boolean scanAllIcons = ConfigAdapter.cfg.getBoolean("scan-all-icons",false);
        ArrayList<String> randomIcons = new ArrayList<>(ConfigAdapter.cfg.getStringList("random-images"));
        if (randomIcons.isEmpty() && !scanAllIcons){
            BukkitPlugin.logger.warning("Random icons list is empty, default server icon will be used");
            icons.add(getDefaultIcon());
            return icons;
        }
        File[] iconsF = iconFolder.listFiles();
        if (iconsF == null){
            BukkitPlugin.logger.warning("Random icons folder is empty, default server icon will be used");
            icons.add(getDefaultIcon());
            return icons;
        }

        for (File f : iconsF){
            if (!f.getName().endsWith(".png")) continue;
            if (!scanAllIcons) if (!randomIcons.contains(f.getName())) continue;
            try {
                BufferedImage img = ImageIO.read(f);
                if (img.getHeight() != 64 || img.getWidth() != 64){
                    BukkitPlugin.logger.warning("Failed to load icon \""+f.getName()+"\" check is icon size is 64x64");
                    continue;
                }
                icons.add(BukkitPlugin.plugin.getServer().loadServerIcon(img));
                BukkitPlugin.logger.info("Loaded icon "+f.getName());
            } catch (IOException e) {
                BukkitPlugin.logger.warning("Failed to load icon \""+f.getName()+"\"");
                e.printStackTrace();
            } catch (Exception e){
                BukkitPlugin.logger.warning("Failed to load icon \""+f.getName()+"\" check is icon size is 64x64");
            }
        }
        if (icons.isEmpty()){
            BukkitPlugin.logger.warning("Random icons list is empty, default server icon will be used");
            icons.add(getDefaultIcon());
        }
        BukkitPlugin.logger.info("Icons loaded");
        return icons;
    }
}
