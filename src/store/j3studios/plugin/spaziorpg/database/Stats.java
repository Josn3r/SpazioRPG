package store.j3studios.plugin.spaziorpg.database;

import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.player.PlayerManager;
import store.j3studios.plugin.spaziorpg.player.SPlayer;

public class Stats {
    
    private static Stats ins;    
    public static Stats get() {
        if (ins == null) {
            ins = new Stats();
        }
        return ins;
    }
    
    /*
        LOAD STATS
    */
    
    public void loadData (Player player) {
        SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
                
        Integer playerLevel = (Integer)SQL.get().getData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL, "uuid", player.getUniqueId().toString());
        Integer playerExp = (Integer)SQL.get().getData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP, "uuid", player.getUniqueId().toString());
        Integer playerTotalExp = (Integer)SQL.get().getData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_TOTAL_EXP, "uuid", player.getUniqueId().toString());
        
        sp.setLevel(playerLevel);
        sp.setExp(playerExp);
        sp.setTotalExp(playerTotalExp);
    }
    
}
