package xyz.bobkinn_.indigomotd;

import net.md_5.bungee.api.connection.PendingConnection;

import java.util.Map;

public class OptiHelper {
    public int online;
    public int max;
    public int visibleMax;
    public PendingConnection player;
    public Map<String,String> plMap;

    public OptiHelper(int max, int online, PendingConnection player,int visibleMax, Map<String,String> plMap){
        this.online=online;
        this.player=player;
        this.max=max;
        this.visibleMax=visibleMax;
        this.plMap=plMap;
    }
}
