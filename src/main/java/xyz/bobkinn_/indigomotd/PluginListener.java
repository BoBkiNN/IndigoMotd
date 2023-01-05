package xyz.bobkinn_.indigomotd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.*;

public class PluginListener implements Listener {
    public ArrayList<Favicon> icons = new ArrayList<>();
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
        protoName = Util.color(protoName);
        playersTop = new ArrayList<>(ConfigAdapter.cfg.getStringList("players.top"));
        if (playersTop.isEmpty()) playersTop = new ArrayList<>(Collections.singletonList("&bCurrent Online: %online_global%/%max%"));
        playerEntry = ConfigAdapter.cfg.getString("players.entry","&9%name%");
        playersServer = ConfigAdapter.cfg.getString("players.playersServer","GLOBAL");
        displayNames = ConfigAdapter.cfg.getBoolean("players.displayNames",false);

        icons = Util.loadIcons();
    }

    public static Map<String,String> getPlaceholdersMap(OptiHelper h){
        Map<String,String> ret = new HashMap<>();
        PendingConnection player = h.player;
        ret.put("%max%", String.valueOf(h.max));
        ret.put("%online%", String.valueOf(h.online));
        for (Map.Entry<String,ServerInfo> server : IndigoMOTD.plugin.getProxy().getServers().entrySet()){
            ret.put("%online_"+server.getKey()+"%", String.valueOf(server.getValue().getPlayers().size()));
        }
        if (player != null){
            ret.put("%playerIP%",player.getSocketAddress().toString());
            if (player.getUniqueId() != null) ret.put("%playerUUID%",player.getUniqueId().toString());
            ret.put("%playerProtocol%", String.valueOf(player.getVersion()));
        }
        return ret;
    }



    public String getDesc(){
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
        return Util.color(line1) + ChatColor.RESET + '\n' + Util.color(line2);
    }

    public static int getVisibleMax(){
        ArrayList<ListenerInfo> listeners = new ArrayList<>(IndigoMOTD.plugin.getProxy().getConfig().getListeners());
        try {
            ListenerInfo listenerInfo = listeners.get(0);
            int maxVisibleOnline;
            maxVisibleOnline = listenerInfo.getMaxPlayers();
            return maxVisibleOnline;
        } catch (IndexOutOfBoundsException ignored) {return -1;}
    }

    public static int getOnline(OptiHelper h){
        String expression = ConfigAdapter.cfg.getString("players.online","%max%-1");
        int fakeOnline = ConfigAdapter.cfg.getInt("players.fake-online",99);
        expression = expression.replace("%max%", String.valueOf(h.max))
                .replace("%limit%", String.valueOf(IndigoMOTD.plugin.getProxy().getConfig().getPlayerLimit()))
                .replace("%fake%",String.valueOf(fakeOnline))
                .replace("%realOnline%",String.valueOf(IndigoMOTD.plugin.getProxy().getOnlineCount()))
                .replace("%visibleMax%",String.valueOf(h.visibleMax));
        Expression e = new Expression(expression);
        double result = e.calculate();
//        IndigoMOTD.logger.info(ChatColor.GREEN+"Online calc "+e.getExpressionString()+" "+result);
        if (Double.isNaN(result)) return IndigoMOTD.plugin.getProxy().getOnlineCount();
        return new Double(result).intValue();
    }

    public static int getMaxOnline(OptiHelper h){
        int fakeMax = ConfigAdapter.cfg.getInt("players.fake-max-online",100);
        String expression = ConfigAdapter.cfg.getString("players.max","%fake%");
        expression = expression.replace("%limit%", String.valueOf(IndigoMOTD.plugin.getProxy().getConfig().getPlayerLimit()))
                .replace("%visibleMax%",String.valueOf(h.visibleMax))
                .replace("%realOnline%",String.valueOf(IndigoMOTD.plugin.getProxy().getOnlineCount()))
                .replace("%fake%",String.valueOf(fakeMax));
        Expression e = new Expression(expression);
        double result = e.calculate();
//        IndigoMOTD.logger.info(ChatColor.GREEN+"Max calc "+e.getExpressionString()+" = "+result);
        if (Double.isNaN(result)) return IndigoMOTD.plugin.getProxy().getConfig().getPlayerLimit();
        return new Double(result).intValue();
    }

    public ArrayList<String> getPlayersTop(OptiHelper player){
        ArrayList<String> ret = new ArrayList<>();
        for (String entry : playersTop){
            String parsed = BaseUtils.parsePlaceholders(entry,player.plMap);
            ret.add(Util.color(parsed));
        }
        return ret;
    }

    @EventHandler
    public void onPing(ProxyPingEvent e) {
        ServerPing ping = e.getResponse();
        ServerPing.Players pl = ping.getPlayers();
        OptiHelper retH = new OptiHelper(1,-1,e.getConnection(),getVisibleMax(),null);
        retH.max=getMaxOnline(retH);
        retH.online=getOnline(retH);
        retH.plMap=getPlaceholdersMap(retH);
        if (changeOnline) pl.setOnline(retH.online);
        if (changeMax) pl.setMax(retH.max);
        if (changeIcon) ping.setFavicon(Util.getRandomIcon(icons));
        if (changeMOTD) ping.setDescriptionComponent(new TextComponent(getDesc()));
        if (changeProtocol){
            ServerPing.Protocol p = ping.getVersion();
            if (changeProtoVer) p.setProtocol(protoVer);
            if (changeProtoName){
                String name = BaseUtils.parsePlaceholders(protoName,retH.plMap);
                p.setName(name);
            }
            ping.setVersion(p);
        }
        if (changePlayers){
            ArrayList<ServerPing.PlayerInfo> players = new ArrayList<>();
            UUID uuid = new UUID(0L,0L);
            for (String entry : getPlayersTop(retH)){
                players.add(new ServerPing.PlayerInfo(entry,uuid));
            }
            if (displayNames){
                for (ProxiedPlayer player : IndigoMOTD.plugin.getProxy().getPlayers()){
                    String entry = Util.color(playerEntry.replace("%name%",player.getName()));
                    if (playersServer.equals("GLOBAL")){
                        players.add(new ServerPing.PlayerInfo(entry,player.getUniqueId()));
                    } else {
                        if (player.getServer().getInfo().getName().equals(playersServer)){
                            players.add(new ServerPing.PlayerInfo(entry,player.getUniqueId()));
                        }
                    }
                }
            }
//            IndigoMOTD.logger.info(players.toString());

            ServerPing.PlayerInfo[] pArr = new ServerPing.PlayerInfo[players.size()];
            for (int i = 0; i<pArr.length;i++){
                pArr[i] = players.get(i);
            }
            pl.setSample(pArr);
        }
        e.setResponse(ping);
    }
}
