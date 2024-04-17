package store.j3studios.plugin.spaziorpg.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import store.j3studios.plugin.spaziorpg.database.SQL;
import store.j3studios.plugin.spaziorpg.player.PlayerManager;
import store.j3studios.plugin.spaziorpg.player.SPlayer;

public class PlayerListener implements Listener {
    
    @EventHandler
    public void onPlayerJoinEvent (PlayerJoinEvent e) {
        Player player = e.getPlayer();
        for (SQL.DataType type : SQL.DataType.values()) {
            if (type.toString().contains("PLAYER_")) {
                SQL.get().createPlayer(type, player.getUniqueId().toString());
            }
        }
        
        if (!PlayerManager.get().isPlayerExists(player.getUniqueId())) {
            PlayerManager.get().createPlayer(player);
        }
        SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
        sp.createBossbar();
    }
    
    public void onPlayerQuitEvent (PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (PlayerManager.get().isPlayerExists(player.getUniqueId())) {
            SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
            sp.removeBossbar();
        }
        PlayerManager.get().removePlayer(player);
    }
    
}
