package xyz.bobkinn_.indigomotd;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.logging.Logger;

public class ConfigAdapter {
    public static Configuration cfg;

    public static void reload(File dataFolder, InputStream configStream, Logger logger){
        if (!dataFolder.exists()){
            boolean created = dataFolder.mkdir();
            if (!created){
                logger.severe("Failed to create data folder "+dataFolder.getAbsolutePath());
                return;
            }
        }
        File configFile = new File(dataFolder,"config.yml");
        try {
            if (!configFile.exists()){
                boolean created = configFile.createNewFile();
                if (!created) {
                    logger.severe("Failed to create config file");
                    return;
                }
                OutputStream to = new FileOutputStream(configFile);
                IOUtils.copy(configStream,to);
            }
            cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
