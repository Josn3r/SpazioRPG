package store.j3studios.plugin.spaziorpg.control;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.database.SQL;
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
        Integer playerLevel = (Integer)SQL.get().getData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL, "uuid", player.getUniqueId().toString());
        return playerLevel * 750;
    }
    
    public void giveExp (Player player, Integer experience) {
        SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
        Integer playerLevel = (Integer)SQL.get().getData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL, "uuid", player.getUniqueId().toString());
        Integer playerExp = (Integer)SQL.get().getData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP, "uuid", player.getUniqueId().toString());
        Integer playerTotalExp = (Integer)SQL.get().getData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_TOTAL_EXP, "uuid", player.getUniqueId().toString());
        Integer maxExp = playerLevel * 750;        
        if ((playerExp+experience) < maxExp) {
            SQL.get().updateData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP, "uuid", player.getUniqueId().toString(), playerExp+experience);
            SQL.get().updateData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_TOTAL_EXP, "uuid", player.getUniqueId().toString(), playerTotalExp+experience);
        } else {
            Integer sobrante = (playerExp+experience)-maxExp;
            Integer giving = experience-sobrante;
            SQL.get().updateData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP, "uuid", player.getUniqueId().toString(), playerExp+giving);
            SQL.get().updateData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_TOTAL_EXP, "uuid", player.getUniqueId().toString(), playerTotalExp+giving);
            checkLevelup(player);
            giveExp(player,sobrante);
        }
        sp.updateBossbar();
    }
    
    public void checkLevelup (Player player) {
        SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
        Integer playerLevel = (Integer)SQL.get().getData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL, "uuid", player.getUniqueId().toString());
        if (playerLevel == 60)
            return;
        SQL.get().updateData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL, "uuid", player.getUniqueId().toString(), playerLevel+1);
        SQL.get().updateData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP, "uuid", player.getUniqueId().toString(), 0);
        Tools.get().playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 5.0f, 1.0f);
        sp.updateBossbar();
    }
    
    
}
