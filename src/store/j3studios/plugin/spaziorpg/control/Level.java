package store.j3studios.plugin.spaziorpg.control;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.RPG;
import store.j3studios.plugin.spaziorpg.player.PlayerManager;
import store.j3studios.plugin.spaziorpg.player.SPlayer;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class Level {

    private static Level ins;
    public static Level get() {
        if (ins == null) {
            ins = new Level();
        }
        return ins;
    }
    
    //
    
    public Integer getNeededExp (Player player) {
        SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
        return sp.getLevel() * 750;
    }
    
    public void giveExp (Player player, Integer experience) {
        SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
        Integer playerLevel = sp.getLevel();
        Integer playerExp = sp.getExp();
        Integer playerTotalExp = sp.getTotalExp();
        Integer maxExp = playerLevel * 750;        
        if ((playerExp+experience) < maxExp) {
            sp.setExp(playerExp+experience);
            sp.setTotalExp(playerTotalExp+experience);
        } else {
            Integer sobrante = (playerExp+experience)-maxExp;
            Integer giving = experience-sobrante;
            sp.setExp(playerExp+giving);
            sp.setTotalExp(playerTotalExp+giving);
            checkLevelup(player);
            giveExp(player,sobrante);
        }
        RPG.get().getServer().getScheduler().runTask(RPG.get(), sp::updateBossbar);
    }
    
    public void checkLevelup (Player player) {
        SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
        Integer playerLevel = sp.getLevel();
        if (playerLevel == 60)
            return;
        sp.setLevel(playerLevel+1);
        sp.setExp(0);            
        Tools.get().playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 5.0f, 1.0f);
        RPG.get().getServer().getScheduler().runTask(RPG.get(), sp::updateBossbar);
    }
    
    
}
