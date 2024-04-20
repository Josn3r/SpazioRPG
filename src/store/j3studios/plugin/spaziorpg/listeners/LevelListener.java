package store.j3studios.plugin.spaziorpg.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import store.j3studios.plugin.spaziorpg.control.Level;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class LevelListener implements Listener {
    
    @EventHandler
    public void onPlayerDeathEvent (PlayerDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            Player player = e.getEntity().getKiller();
            Level.get().giveExp(player, 25);
            
            Tools.debug(Tools.DebugType.LOG, "" + player.getName() + " killed " + e.getEntity().toString());
        }
    }
    
    @EventHandler
    public void onEntityDeathEvent (EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            Player player = e.getEntity().getKiller();
            Level.get().giveExp(player, 5);
            
            Tools.debug(Tools.DebugType.LOG, "" + player.getName() + " killed " + e.getEntity().toString());
        }
    }
    
}
