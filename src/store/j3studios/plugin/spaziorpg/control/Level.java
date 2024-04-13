package store.j3studios.plugin.spaziorpg.control;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.database.SQL;
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
        Integer playerLevel = (Integer)SQL.get().getPlayer(player, SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL);
        return playerLevel * 750;
    }
    
    public void giveExp (Player player, Integer experience) {
        Integer playerLevel = (Integer) SQL.get().getPlayer(player, SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL);
        Integer playerExp = (Integer) SQL.get().getPlayer(player, SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP);
        Integer playerTotalExp = (Integer) SQL.get().getPlayer(player, SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_TOTAL_EXP);
        Integer maxExp = playerLevel * 750;        
        if ((playerExp+experience) < maxExp) {
            SQL.get().updatePlayer(player, SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP, playerExp+experience);
            SQL.get().updatePlayer(player, SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_TOTAL_EXP, playerTotalExp+experience);
        } else {
            Integer sobrante = (playerExp+experience)-maxExp;
            Integer giving = experience-sobrante;
            SQL.get().updatePlayer(player, SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP, playerExp+giving);
            SQL.get().updatePlayer(player, SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_TOTAL_EXP, playerTotalExp+giving);
            checkLevelup(player);
            giveExp(player,sobrante);
        }
    }
    
    public void checkLevelup (Player player) {
        Integer playerLevel = (Integer) SQL.get().getPlayer(player, SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL);
        if (playerLevel == 60)
            return;
        SQL.get().updatePlayer(player, SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL, playerLevel+1);
        SQL.get().updatePlayer(player, SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP, 0);
        Tools.get().playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 5.0f, 1.0f);
    }
    
    
}
