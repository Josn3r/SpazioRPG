package store.j3studios.plugin.spaziorpg;

import org.bukkit.plugin.java.JavaPlugin;
import store.j3studios.plugin.spaziorpg.utils.Config;

public class RPG extends JavaPlugin {
    
    private static RPG ins;
    
    @Override
    public void onEnable() {
        ins = this;
        
        // Loading configuration files
        getConfig();
        saveDefaultConfig();        
        final Config load = new Config(this, "lang_us");
        load.getConfig();
    }
    
    @Override
    public void onDisable() {
        
    }
    
    //
    
    public static RPG get() {
        return ins;
    }
    
}
