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
        Integer playerLevel = (Integer)SQL.get().getPlayer(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL, SQL.UpdateType.PLAYER_STATS_UUID, player.getUniqueId().toString());
        return playerLevel * 750;
    }
    
    public void giveExp (Player player, Integer experience) {
        SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
        Integer playerLevel = (Integer) SQL.get().getPlayer(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL, SQL.UpdateType.PLAYER_STATS_UUID, player.getUniqueId().toString());
        Integer playerExp = (Integer) SQL.get().getPlayer(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP, SQL.UpdateType.PLAYER_STATS_UUID, player.getUniqueId().toString());
        Integer playerTotalExp = (Integer) SQL.get().getPlayer(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_TOTAL_EXP, SQL.UpdateType.PLAYER_STATS_UUID, player.getUniqueId().toString());
        Integer maxExp = playerLevel * 750;        
        if ((playerExp+experience) < maxExp) {
            SQL.get().updatePlayer(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP, playerExp+experience, player.getUniqueId().toString());
            SQL.get().updatePlayer(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_TOTAL_EXP, playerTotalExp+experience, player.getUniqueId().toString());
        } else {
            Integer sobrante = (playerExp+experience)-maxExp;
            Integer giving = experience-sobrante;
            SQL.get().updatePlayer(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP, playerExp+giving, player.getUniqueId().toString());
            SQL.get().updatePlayer(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_TOTAL_EXP, playerTotalExp+giving, player.getUniqueId().toString());
            checkLevelup(player);
            giveExp(player,sobrante);
        }
        sp.updateBossbar();
    }
    
    public void checkLevelup (Player player) {
        SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
        Integer playerLevel = (Integer) SQL.get().getPlayer(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL, SQL.UpdateType.PLAYER_STATS_UUID, player.getUniqueId().toString());
        if (playerLevel == 60)
            return;
        SQL.get().updatePlayer(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL, playerLevel+1, player.getUniqueId().toString());
        SQL.get().updatePlayer(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP, 0, player.getUniqueId().toString());
        Tools.get().playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 5.0f, 1.0f);
        sp.updateBossbar();
    }
    
    
}
