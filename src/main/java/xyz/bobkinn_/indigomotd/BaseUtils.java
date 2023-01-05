package xyz.bobkinn_.indigomotd;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map;

public class BaseUtils {
    public static boolean linesEmpty(){
        ArrayList<String> l1 = new ArrayList<>(ConfigAdapter.cfg.getStringList("motd.lineOne"));
        ArrayList<String> l2 = new ArrayList<>(ConfigAdapter.cfg.getStringList("motd.lineTwo"));
        return l1.isEmpty() || l2.isEmpty();
    }

    public static boolean linesCountSame(){
        ArrayList<String> l1 = new ArrayList<>(ConfigAdapter.cfg.getStringList("motd.lineOne"));
        ArrayList<String> l2 = new ArrayList<>(ConfigAdapter.cfg.getStringList("motd.lineTwo"));
        return l1.size() == l2.size();
    }

    public static String parsePlaceholders(String original,@Nonnull Map<String,String> placeholderMap){
        IndigoMOTD.logger.info("=== Parsing \""+original+"\"...");
        String ret = original;
        for (Map.Entry<String,String> entry : placeholderMap.entrySet()){
            if (entry.getValue() == null){
                IndigoMOTD.logger.warning("Failed to replace "+entry.getKey()+" because value is null");
                continue;
            }
            IndigoMOTD.logger.info(entry.getKey()+", "+entry.getValue());
            ret =ret.replace(entry.getKey() , entry.getValue());
        }
        IndigoMOTD.logger.info("--- Parsed: \""+ret+"\"");
        return ret;
    }
}
