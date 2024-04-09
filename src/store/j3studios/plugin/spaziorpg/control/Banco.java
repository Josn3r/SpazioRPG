package store.j3studios.plugin.spaziorpg.control;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public class Banco {
    
    private static Banco ins;    
    public static Banco get() {
        if (ins == null) { ins = new Banco(); }
        return ins;
    }
    
    //
    
    private final Map<Player, Double> paydayLog = new HashMap<>();
    public Map<Player, Double> getPaydayLog() { return paydayLog; }
    
    
    
}
