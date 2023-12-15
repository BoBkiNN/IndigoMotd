package xyz.bobkinn_.indigomotd;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

    public static net.md_5.bungee.api.chat.ClickEvent.Action toBungee(ClickEvent.@NotNull Action action){
        switch (action){
            case OPEN_URL: return net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL;
            case OPEN_FILE: return net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_FILE;
            case CHANGE_PAGE: return net.md_5.bungee.api.chat.ClickEvent.Action.CHANGE_PAGE;
            case COPY_TO_CLIPBOARD: return net.md_5.bungee.api.chat.ClickEvent.Action.COPY_TO_CLIPBOARD;
            case RUN_COMMAND: return net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND;
            case SUGGEST_COMMAND: return net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND;
            default: return null;
        }
    }

    public static net.md_5.bungee.api.chat.HoverEvent.Action toBungee(HoverEvent.Action<?> action){
        Class<?> type = action.type();
        if (type.equals(Component.class)) {
            return net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT;
        } else if (type.equals(HoverEvent.ShowItem.class)) {
            return net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ITEM;
        } else if (type.equals(HoverEvent.ShowEntity.class)) {
            return net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ENTITY;
        }
        return null;
    }

    public static net.md_5.bungee.api.chat.BaseComponent[] toList(BaseComponent comp){
        List<BaseComponent> extra = comp.getExtra();
        comp.setExtra(new ArrayList<>(0));
        BaseComponent[] ret = new BaseComponent[1+extra.size()];
        ret[0] = comp;
        for (int i = 0; i < extra.size(); i++){
            ret[i+1] = extra.get(i);
        }
        return ret;
    }

    @SuppressWarnings("deprecation")
    public static net.md_5.bungee.api.chat.BaseComponent toBungee(Component adventure){
        if (adventure == null) return null;
        net.md_5.bungee.api.chat.BaseComponent comp;
        if (adventure instanceof TextComponent){
            TextComponent text = (TextComponent) adventure;
            comp = new net.md_5.bungee.api.chat.TextComponent(text.content());
        } else if (adventure instanceof TranslatableComponent) {
            TranslatableComponent translate = (TranslatableComponent) adventure;
            comp = new net.md_5.bungee.api.chat.TranslatableComponent(translate.key(),
                    translate.args().stream().map(Util::toBungee).collect(Collectors.toList()));
        } else if (adventure instanceof ScoreComponent) {
            ScoreComponent score = (ScoreComponent) adventure;
            comp = new net.md_5.bungee.api.chat.ScoreComponent(score.name(), score.objective(), score.value());
        } else if (adventure instanceof KeybindComponent){
            KeybindComponent key = (KeybindComponent) adventure;
            comp = new net.md_5.bungee.api.chat.KeybindComponent(key.keybind());
        } else if (adventure instanceof SelectorComponent) {
            SelectorComponent selector = (SelectorComponent) adventure;
            comp = new net.md_5.bungee.api.chat.SelectorComponent(selector.pattern());
        } else {
            throw new UnsupportedOperationException("unknown class "+adventure.getClass().getName());
        }
        TextDecoration.State bold = adventure.decoration(TextDecoration.BOLD);
        if (bold == TextDecoration.State.TRUE){
            comp.setBold(true);
        } else if (bold == TextDecoration.State.FALSE) {
            comp.setBold(false);
        }
        TextDecoration.State italic = adventure.decoration(TextDecoration.ITALIC);
        if (italic == TextDecoration.State.TRUE){
            comp.setItalic(true);
        } else if (italic == TextDecoration.State.FALSE) {
            comp.setItalic(false);
        }
        TextDecoration.State obfuscated = adventure.decoration(TextDecoration.OBFUSCATED);
        if (obfuscated == TextDecoration.State.TRUE){
            comp.setObfuscated(true);
        } else if (obfuscated == TextDecoration.State.FALSE) {
            comp.setObfuscated(false);
        }
        TextDecoration.State strike = adventure.decoration(TextDecoration.STRIKETHROUGH);
        if (strike == TextDecoration.State.TRUE){
            comp.setStrikethrough(true);
        } else if (strike == TextDecoration.State.FALSE) {
            comp.setStrikethrough(false);
        }
        TextDecoration.State underlined = adventure.decoration(TextDecoration.UNDERLINED);
        if (underlined == TextDecoration.State.TRUE){
            comp.setUnderlined(true);
        } else if (underlined == TextDecoration.State.FALSE) {
            comp.setUnderlined(false);
        }
        TextColor color = adventure.color();
        if (color != null){
            comp.setColor(ChatColor.of(new Color(color.value())));
        }
        ClickEvent clickEvent = adventure.clickEvent();
        if (clickEvent != null){
            net.md_5.bungee.api.chat.ClickEvent.Action act = toBungee(clickEvent.action());
            if (act != null) comp.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(act, clickEvent.value()));
        }
        HoverEvent<?> hoverEvent = adventure.hoverEvent();
        if (hoverEvent != null){
            net.md_5.bungee.api.chat.HoverEvent.Action act = toBungee(hoverEvent.action());
            Object value = hoverEvent.value();
            Content content;
            if (value instanceof Component){
                BaseComponent tv = toBungee((Component) value);
                content = new Text(toList(tv.duplicate()));
            } else if (value instanceof HoverEvent.ShowItem) {
                HoverEvent.ShowItem item = (HoverEvent.ShowItem) value;
                BinaryTagHolder binaryTagHolder = item.nbt();
                ItemTag tag = ItemTag.ofNbt(binaryTagHolder == null ? "{}" : binaryTagHolder.string());
                content = new Item(item.item().asString(), item.count(), tag);
            } else if (value instanceof HoverEvent.ShowEntity) {
                HoverEvent.ShowEntity entity = (HoverEvent.ShowEntity) value;
                content = new Entity(entity.type().asString(), entity.id().toString(), toBungee(entity.name()));
            } else {
                throw new UnsupportedOperationException("Unknown hover event "+value.getClass().getName());
            }
            comp.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(act, content));
        }
        Key font = adventure.font();
        if (font != null){
            comp.setFont(font.asString());
        }
        String insertion = adventure.insertion();
        if (insertion != null) comp.setInsertion(insertion);
        for (Component e : adventure.children()){
            comp.addExtra(toBungee(e));
        }
        return comp;
    }

    public static net.md_5.bungee.api.chat.BaseComponent parseMiniMessage(String text){
        try {
            Component comp = MiniMessage.miniMessage().deserialize(text);
            return toBungee(comp);
        } catch (Exception e){
//            e.printStackTrace();
            return legacyToBungee(text);
        }

    }

    public static LegacyComponentSerializer serializer = LegacyComponentSerializer.builder()
            .character('&').extractUrls().hexColors().extractUrls().build();

    public static net.md_5.bungee.api.chat.BaseComponent legacyToBungee(String text){
        return toBungee(serializer.deserialize(text));
    }

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
//                IndigoMOTD.logger.info("Loaded icon "+f.getName());
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
//        IndigoMOTD.logger.info("Icons loaded");
        return icons;
    }
}
