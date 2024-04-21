package store.j3studios.plugin.spaziorpg.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import store.j3studios.plugin.spaziorpg.control.Level;

public class LevelListener implements Listener {
    
    @EventHandler
    public void onPlayerDeathEvent (PlayerDeathEvent e) {
        if (e.getEntity().getKiller() instanceof Player) {
            Player player = e.getEntity().getKiller();
            Level.get().giveExp(player, 25);
        }
    }
    
    @EventHandler
    public void onEntityDeathEvent (EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            if (e.getEntity().getKiller() instanceof Player) {
                Player player = e.getEntity().getKiller();
                Level.get().giveExp(player, 2);
            }
        }
    }
    
}
