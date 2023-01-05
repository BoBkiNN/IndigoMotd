package xyz.bobkinn_.indigomotd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Util {
    public static Favicon getDefaultIcon(){
        return IndigoMOTD.plugin.getProxy().getConfig().getFaviconObject();
    }



    public static Favicon getRandomIcon(List<Favicon> icons){
        int r = new Random().nextInt(icons.size());
        return icons.get(r);
    }

    public static String color(String text){
        return ChatColor.translateAlternateColorCodes('&',text);
    }

//    public static String translateHexColorCodes(String message)
//    {
//        String startTag = "&#";
//        String endTag = "";
//        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
//        Matcher matcher = hexPattern.matcher(message);
//        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
//        while (matcher.find())
//        {
//            String group = matcher.group(1);
//            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
//                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
//                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
//                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
//            );
//        }
//        return matcher.appendTail(buffer).toString();
//    }

    public static ArrayList<Favicon> loadIcons(){
        ArrayList<Favicon> icons = new ArrayList<>();
        IndigoMOTD.logger.info("Loading icons..");
        File iconFolder = new File(IndigoMOTD.plugin.getDataFolder(),"icons");
        if (!iconFolder.exists()){
            if(!iconFolder.mkdirs()){
                icons.add(getDefaultIcon());
                return icons;
            }
        }
        if (!iconFolder.isDirectory()){
            IndigoMOTD.logger.severe("\"icons\" is not directory, failed to load icons");
            icons.add(getDefaultIcon());
            return icons;
        }
        boolean scanAllIcons = ConfigAdapter.cfg.getBoolean("scan-all-icons",false);
        ArrayList<String> randomIcons = new ArrayList<>(IndigoMOTD.cfg.getStringList("random-images"));
        if (randomIcons.isEmpty() && !scanAllIcons){
            IndigoMOTD.logger.warning("Random icons list is empty, default server icon will be used");
            icons.add(getDefaultIcon());
            return icons;
        }
        File[] iconsF = iconFolder.listFiles();
        if (iconsF == null){
            IndigoMOTD.logger.warning("Random icons folder is empty, default server icon will be used");
            icons.add(getDefaultIcon());
            return icons;
        }

        for (File f : iconsF){
            if (!f.getName().endsWith(".png")) continue;
            if (!scanAllIcons) if (!randomIcons.contains(f.getName())) continue;
            try {
                BufferedImage img = ImageIO.read(f);
                icons.add(Favicon.create(img));
                IndigoMOTD.logger.info("Loaded icon "+f.getName());
            } catch (IOException e) {
                IndigoMOTD.logger.warning("Failed to load icon \""+f.getName()+"\"");
                e.printStackTrace();
            } catch (IllegalArgumentException e){
                IndigoMOTD.logger.warning("Failed to load icon \""+f.getName()+"\" check is icon size is 64x64");
            }
        }
        if (icons.isEmpty()){
            IndigoMOTD.logger.warning("Random icons list is empty, default server icon will be used");
            icons.add(IndigoMOTD.plugin.getProxy().getConfig().getFaviconObject());
        }
        IndigoMOTD.logger.info("Icons loaded");
        return icons;
    }
}
