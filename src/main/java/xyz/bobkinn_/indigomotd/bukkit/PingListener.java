package xyz.bobkinn_.indigomotd.bukkit;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;
import org.mariuszgromada.math.mxparser.Expression;
import xyz.bobkinn_.indigomotd.BaseUtils;
import xyz.bobkinn_.indigomotd.ConfigAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PingListener implements Listener {
    ArrayList<CachedServerIcon> icons = new ArrayList<>();
    public boolean changeOnline;
    public boolean changeMax;
    public boolean changePlayers;
    public boolean changeIcon;
    public boolean changeMOTD;
    public boolean changeProtoName;
    public boolean changeProtoVer;
    public boolean changeProtocol;
    public int protoVer;
    public String protoName;
    public ArrayList<String> playersTop;
    public String playerEntry;
    public String playersServer;
    public boolean displayNames;

    public void reload(){
        changeOnline = ConfigAdapter.cfg.getBoolean("change.online",true);
        changeMax = ConfigAdapter.cfg.getBoolean("change.max",true);
        changePlayers = ConfigAdapter.cfg.getBoolean("change.players",true);
        changeIcon = ConfigAdapter.cfg.getBoolean("change.icon",true);
        changeMOTD = ConfigAdapter.cfg.getBoolean("change.motd",true);
        changeProtoVer = ConfigAdapter.cfg.getBoolean("change.protocol-ver",true);
        changeProtoName = ConfigAdapter.cfg.getBoolean("change.protocol-name",true);
        changeProtocol = ConfigAdapter.cfg.getBoolean("change.protocol",true);
        protoVer = ConfigAdapter.cfg.getInt("protocol-ver",758);
        protoName = ConfigAdapter.cfg.getString("protocol-name","Custom Protocol Name");
        protoName = Utils.color(protoName);
        playersTop = new ArrayList<>(ConfigAdapter.cfg.getStringList("players.top"));
        if (playersTop.isEmpty()) playersTop = new ArrayList<>(Collections.singletonList("&bCurrent Online: %online_global%/%max%"));
        playerEntry = ConfigAdapter.cfg.getString("players.entry","&9%name%");
        playersServer = ConfigAdapter.cfg.getString("players.playersServer","GLOBAL");
        displayNames = ConfigAdapter.cfg.getBoolean("players.displayNames",false);

        icons = Utils.loadIcons();
        ArrayList<String> l1 = new ArrayList<>(ConfigAdapter.cfg.getStringList("motd.lineOne"));
        ArrayList<String> l2 = new ArrayList<>(ConfigAdapter.cfg.getStringList("motd.lineTwo"));
        if (BaseUtils.linesEmpty()){
            if (l2.isEmpty()){
                ConfigAdapter.cfg.set("motd.lineTwo",Collections.singletonList("Line 2"));
            } else if (l1.isEmpty()) {
                ConfigAdapter.cfg.set("motd.lineOne",Collections.singletonList("Line 1"));
            } else {
                ConfigAdapter.cfg.set("motd.lineOne",Collections.singletonList("Line 1"));
                ConfigAdapter.cfg.set("motd.lineTwo",Collections.singletonList("Line 2"));
            }
        }
        if (l1.size() != l2.size()){
            BukkitPlugin.logger.severe("Lines count are not same, lineTwo will be similar as lineOne");
            ConfigAdapter.cfg.set("motd.lineTwo",l1);
        }
    }

    public String getMOTD(){
        ArrayList<String> l1 = new ArrayList<>(ConfigAdapter.cfg.getStringList("motd.lineOne"));
        ArrayList<String> l2 = new ArrayList<>(ConfigAdapter.cfg.getStringList("motd.lineTwo"));
        String line1;
        String line2;
        boolean splitRandom = ConfigAdapter.cfg.getBoolean("motd.split-random",false);
        if (!splitRandom){
            int i = new Random().nextInt(l1.size());
            line1 = l1.get(i);
            line2 = l2.get(i);
        } else {
            int i1 = new Random().nextInt(l1.size());
            int i2 = new Random().nextInt(l2.size());
            line1 = l1.get(i1);
            line2 = l2.get(i2);
        }
        return  line1+'\n'+line2;
    }

    public static int getMaxOnline(){
        int fakeMax = ConfigAdapter.cfg.getInt("players.fake-max-online",100);
        String expression = ConfigAdapter.cfg.getString("players.max","%fake%");
        expression = expression.replace("%realMax%", String.valueOf(BukkitPlugin.plugin.getServer().getMaxPlayers()))
                .replace("%realOnline%",String.valueOf(BukkitPlugin.plugin.getServer().getOnlinePlayers().size()))
                .replace("%fake%",String.valueOf(fakeMax));
        Expression e = new Expression(expression);
        double result = e.calculate();
        BukkitPlugin.logger.info(ChatColor.GREEN+"Max calc "+e.getExpressionString()+" = "+result);
        if (Double.isNaN(result)) return BukkitPlugin.plugin.getServer().getMaxPlayers();
        return new Double(result).intValue();
    }


    public static int getOnline(OptiHelperB h){
        String expression = ConfigAdapter.cfg.getString("players.online","%max%-1");
        int fakeOnline = ConfigAdapter.cfg.getInt("players.fake-online",99);
        expression = expression.replace("%max%", String.valueOf(h.maxOnline))
                .replace("%fake%",String.valueOf(fakeOnline))
                .replace("%realOnline%",String.valueOf(BukkitPlugin.plugin.getServer().getMaxPlayers()));
        Expression e = new Expression(expression);
        double result = e.calculate();
        BukkitPlugin.logger.info(ChatColor.GREEN+"Online calc "+e.getExpressionString()+" "+result);
        if (Double.isNaN(result)) return BukkitPlugin.plugin.getServer().getOnlinePlayers().size();
        return new Double(result).intValue();
    }


    @EventHandler
    public void onPing(ServerListPingEvent e){
        BukkitPlugin.logger.info("Pinged");
        OptiHelperB h = new OptiHelperB(0,getMaxOnline());
        h.online=getOnline(h);
        if (changeIcon) e.setServerIcon(Utils.getRandomIcon(icons));
        if (changeMOTD) e.setMotd(getMOTD());
        if (changeMax) e.setMaxPlayers(h.maxOnline);


    }
}
