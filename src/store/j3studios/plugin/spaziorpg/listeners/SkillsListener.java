package store.j3studios.plugin.spaziorpg.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import store.j3studios.plugin.spaziorpg.player.PlayerManager;
import store.j3studios.plugin.spaziorpg.player.SPlayer;

public class SkillsListener implements Listener {
    
    @EventHandler
    public void onEntityDamageByEntityEvent (EntityDamageByEntityEvent e) {
        if (e.isCancelled()) 
            return;
        if (e.getEntity() instanceof Player) {
            Player player = (Player)e.getEntity();
            SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
            
            Double damage = e.getDamage();
            if (sp.getLife() > 20.0) {
                e.setDamage(0.0D);
                sp.setLife(sp.getLife()-damage);
            } else {
                sp.setLife(sp.getLife()-damage);
                if (sp.getLife()<0.0) {
                    sp.setLife(0.0d);
                    e.setDamage(10000.0);
                }
            }
        }
    }
    
}
