package store.j3studios.plugin.spaziorpg.utils;

import com.google.common.base.Strings;
import java.text.DecimalFormat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.database.SQL;

public class Tools {
    
    private static Tools ins;
    
    public static Tools get() {
        if (ins == null) {
            ins = new Tools();
        }
        return ins;
    }
    
    /*
        Colour translate.
    */
    
    public static String PREFIX = "&7[&e&lM&6&lS&7] » ";
    
    public static String Text (String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
    
    public static void debug (DebugType type, String str) {
        if (null != type) switch (type) {
            case SUCCESS -> Bukkit.getConsoleSender().sendMessage(Text("&7sRPG &8- &7[&a&lSUCCESS&7] " + str));
            case WARNING -> Bukkit.getConsoleSender().sendMessage(Text("&7sRPG &8- &7[&6&lWARNING&7] " + str));
            case ERROR -> Bukkit.getConsoleSender().sendMessage(Text("&7sRPG &8- &7[&c&lERROR&7] " + str));
            case LOG -> Bukkit.getConsoleSender().sendMessage(Text("&7sRPG &8- &7[&fLOG&7] " + str));
            default -> {
            }
        }
    }
    
    public enum DebugType {
        SUCCESS,
        WARNING,
        ERROR,
        LOG;
    }
    
    /*
        Format's // Messages
    */
    
    public String decimalFormat (Object value, String formatted) {
        DecimalFormat format = new DecimalFormat(formatted);
        return format.format(value);
    }
    
    public String formatTime (Integer time) {
        Integer hours = time / 3600;
        Integer secondsLeft = time - hours * 3600;
        Integer minutes = secondsLeft / 60;
        Integer seconds = secondsLeft - minutes * 60;
        
        String formatTime = "";
        
        if (hours >= 1) {
            if (hours < 10)
                formatTime += "0";
            formatTime += hours + ":";
        }
        if (minutes >= 1) {
            if (minutes < 10)
                formatTime += "0";
            formatTime += minutes + ":";
            if (seconds < 10)
                formatTime += "0";
            formatTime += seconds;
        } else {
            formatTime += seconds + "s";
        }
        return formatTime;
    }
    
    /*
        Messages options
    */
    
    private final int TAMANO_SERIE = 10; // Longitud del número de serie
    private final String ALFABETO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // Caracteres para generar el número de serie    
    public String getNumSerie() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < TAMANO_SERIE; i++) {
            int indiceAleatorio = (int) (Math.random() * ALFABETO.length());
            builder.append(ALFABETO.charAt(indiceAleatorio));
        }        
        if (SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_ACCOUNT, "account", builder.toString()) != null) {            
            return getNumSerie();
        }        
        return builder.toString();
    }
    
    public String compileWords (String[] args, Integer index) {
        StringBuilder builder = new StringBuilder();
        for (int i = index; i < args.length; ++i) {
            builder.append(args[i]).append(" ");
        }
        return builder.toString().trim();
    }
    
    public void clearChat (Player p, Integer lines) {
        for (int i = 0; i < lines; ++i) {
            p.sendMessage(" ");
        }
    }
    
    public void sendTitle (Player p, String title, String subtitle, Integer fadeIn, Integer stay, Integer fadeOut) {
        p.sendTitle(Text(title), Text(subtitle), fadeIn, stay, fadeOut);
    }
    
    public void sendActionBar (Player p, String str) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Text(str)));
    }
    
    /*
    public void sendClickMessage (Player p, String str, HoverEvent.Action hoverType, String hoverText, ClickEvent.Action clickType, String clickEvent) {
        ComponentBuilder msg = new ComponentBuilder(str);
        BaseComponent[] value = msg.event(new HoverEvent(hoverType, hoverText)).event(new ClickEvent(clickType, clickEvent)).create();
    }
    */
    
    public void sendClickMessage (Player p, String str, ClickEvent.Action clickType, String clickEvent) {
        ComponentBuilder msg = new ComponentBuilder(str);
        BaseComponent[] value = msg.event(new ClickEvent(clickType, clickEvent)).create();
        p.spigot().sendMessage(value);
    }
    
    public String getProgressBar (Integer current, Integer max, Integer totalBars, char symbol, ChatColor completed, ChatColor notCompleted) {
        float percent = (float)current/max;
        int progressBars = (int)(totalBars*percent);
        return Strings.repeat("" + completed + symbol, progressBars) + Strings.repeat("" + notCompleted + symbol, totalBars - progressBars);
    }
    
    /*
        Player Sound / Particle
    */
    
    public void playSound (Player p, Sound sound, Float volumen, Float pitch) {
        p.playSound(p.getLocation(), sound, volumen, pitch);
    }
    
    public void playSound (Location loc, Sound sound, Float volumen, Float pitch) {
        loc.getWorld().playSound(loc, sound, volumen, pitch);
    }
    
    /*
        Calculate Values
    */
    
    public Double calcPercent (Double value, Double percent) {
        return (value * (100 + (-(100-percent))) / 100);
    }
    
    public boolean isInt(String s) {
        try {
            Integer.valueOf(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    public boolean isDouble(String s) {
        try {
            Double.valueOf(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    /*
        OTHERS
    */
    
    public Boolean isBedrock (Player player) {
        if (player.getName().startsWith("db_"))
            return true;
        return false;
    }
    
}
