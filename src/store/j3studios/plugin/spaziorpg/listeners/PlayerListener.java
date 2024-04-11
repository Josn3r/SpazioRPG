package store.j3studios.plugin.spaziorpg.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import store.j3studios.plugin.spaziorpg.database.SQL;
import store.j3studios.plugin.spaziorpg.player.PlayerManager;
import store.j3studios.plugin.spaziorpg.player.SPlayer;

public class PlayerListener implements Listener {
    
    @EventHandler
    public void onPlayerJoinEvent (PlayerJoinEvent e) {
        Player player = e.getPlayer();
        for (SQL.DataType type : SQL.DataType.values()) {
            if (type.toString().contains("PLAYER_")) {
                SQL.get().createPlayer(player, type);
            }
        }
        
        if (!PlayerManager.get().isPlayerExists(player.getUniqueId())) {
            PlayerManager.get().createPlayer(player);
        }
        SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
        sp.createBossbar();
    }
    
}
