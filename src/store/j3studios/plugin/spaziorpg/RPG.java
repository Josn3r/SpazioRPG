package store.j3studios.plugin.spaziorpg;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import store.j3studios.plugin.spaziorpg.control.Skills;
import store.j3studios.plugin.spaziorpg.database.SQL;
import store.j3studios.plugin.spaziorpg.listeners.PlayerListener;
import store.j3studios.plugin.spaziorpg.player.PlayerManager;
import store.j3studios.plugin.spaziorpg.player.SPlayer;
import store.j3studios.plugin.spaziorpg.utils.Config;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class RPG extends JavaPlugin {
    
    private static RPG ins;
    
    @Override
    public void onEnable() {
        ins = this;
        
        // Loading configuration files
        getConfig();
        saveDefaultConfig();        
        final Config load = new Config(this, "lang_us");
        
        // Loading SQL
        SQL.get().openConnection();
        if (SQL.get().getConnection() == null) {
            Tools.debug(Tools.DebugType.ERROR, "MySQL can't connect to database, please check your config.yml.");
        }
        
        // Loading listeners
        registerEvent(new PlayerListener());
        
        // Loading runnables
        getServer().getScheduler().runTaskTimerAsynchronously(RPG.get(), () -> {
           for (Player online : Bukkit.getOnlinePlayers()) {
               if (!PlayerManager.get().isPlayerExists(online.getUniqueId())) {
                   PlayerManager.get().createPlayer(online);
               }
               SPlayer sp = PlayerManager.get().getPlayer(online.getUniqueId());
               sp.updateBossbar();
               
               Tools.get().sendActionBar(online, "&bMan�: &f" + Skills.get().getStats(online, Skills.StatsType.MANA) + "     &7|     &cVida: &f" + Skills.get().getStats(online, Skills.StatsType.LIFE));
           } 
        }, 0, 20);
    }
    
    @Override
    public void onDisable() {
        
    }
    
    //
    
    public static RPG get() {
        return ins;
    }
    
    public void registerEvent (Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
        String listenerName = listener.getClass().getName().replace("store.j3studios.plugin.spaziorpg.listeners.", "");
        Tools.debug(Tools.DebugType.SUCCESS, "&fRegistering &e" + listenerName + " &flistener success.");
    }
    
    
}
