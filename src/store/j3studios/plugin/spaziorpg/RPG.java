package store.j3studios.plugin.spaziorpg;

import org.bukkit.plugin.java.JavaPlugin;

public class RPG extends JavaPlugin {
    
    private static RPG ins;
    
    @Override
    public void onEnable() {
        ins = this;
    }
    
    @Override
    public void onDisable() {
        
    }
    
    //
    
    public static RPG get() {
        return ins;
    }
    
}
