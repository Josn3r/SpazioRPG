package store.j3studios.plugin.spaziorpg;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import store.j3studios.plugin.spaziorpg.cmds.CommandManager;
import store.j3studios.plugin.spaziorpg.database.SQL;
import store.j3studios.plugin.spaziorpg.database.Stats;
import store.j3studios.plugin.spaziorpg.listeners.LevelListener;
import store.j3studios.plugin.spaziorpg.listeners.PlayerListener;
import store.j3studios.plugin.spaziorpg.player.PlayerManager;
import store.j3studios.plugin.spaziorpg.player.SPlayer;
import store.j3studios.plugin.spaziorpg.utils.Config;
import store.j3studios.plugin.spaziorpg.utils.Tools;
import store.j3studios.plugin.spaziorpg.utils.Vault;

public class RPG extends JavaPlugin {
    
    private static RPG ins;
    public static Economy econ;
    
    @Override
    public void onEnable() {
        ins = this;
        
        // Loading configuration files xd
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
        registerEvent(new LevelListener());
        getCommand("spaziorpg").setExecutor(new CommandManager());
        
        Vault.setupEconomy();
        
        // Loading runnaspaziorpgbles
        for (Player online : Bukkit.getOnlinePlayers()) {
            for (SQL.DataType type : SQL.DataType.values()) {
                if (type.toString().contains("PLAYER_")) {
                    SQL.get().createPlayer(type, online.getUniqueId().toString());
                }
            } 
            if (!PlayerManager.get().isPlayerExists(online.getUniqueId())) {
                PlayerManager.get().createPlayer(online);
            }            
            SPlayer sp = PlayerManager.get().getPlayer(online.getUniqueId());
            Stats.get().loadData(online);
            sp.createBossbar();
        }
    }
    
    @Override
    public void onDisable() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (PlayerManager.get().isPlayerExists(online.getUniqueId())) {
                SPlayer sp = PlayerManager.get().getPlayer(online.getUniqueId());
                sp.removeBossbar();
            }
            PlayerManager.get().removePlayer(online);
        }
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
