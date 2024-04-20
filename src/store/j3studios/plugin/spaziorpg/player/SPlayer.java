package store.j3studios.plugin.spaziorpg.player;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.RPG;
import store.j3studios.plugin.spaziorpg.control.Level;
import store.j3studios.plugin.spaziorpg.database.SQL;
import store.j3studios.plugin.spaziorpg.utils.Config;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class SPlayer {
    
    private final UUID uuid;
    private final Player player;
    
    private final BossBar bossBar = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SOLID);   
    
    private Config lang;
    
    // Menu controller
    private Boolean openMenu = false;
    
    public SPlayer (UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
    }

    public Player getPlayer() {
        return player;
    }

    public void setLang (String str) {
        lang = new Config(RPG.get(), str);
    }
    
    public boolean isOpenMenu() {
        return openMenu;
    }
    
    public void setOpenMenu(Boolean value) {
        openMenu = value;
    }
    
    public Config getLang() {
        return lang;
    }    
    
    public BossBar getBossbar() {
        return bossBar;
    }
    
    public void createBossbar() {
        Integer level = (Integer) SQL.get().getData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL, "uuid", uuid.toString());
        Integer exp = (Integer) SQL.get().getData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP, "uuid", uuid.toString());
        Integer needExp = Level.get().getNeededExp(player);
        
        bossBar.setColor(BarColor.YELLOW);
        bossBar.setStyle(BarStyle.SOLID);
        bossBar.setTitle(Tools.Text("&fNivel: &e" + level + " &7- &fExp: &e" + exp + "&7/" + needExp));
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);
        
        bossBar.addPlayer(player);
    }
    
    public void updateBossbar() {
        if (!bossBar.getPlayers().contains(player)) {
            createBossbar();
            return;
        }
        Integer level = (Integer) SQL.get().getData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_LEVEL, "uuid", uuid.toString());
        Integer exp = (Integer) SQL.get().getData(SQL.DataType.PLAYER_STATS, SQL.UpdateType.PLAYER_STATS_EXP, "uuid", uuid.toString());
        Integer needExp = Level.get().getNeededExp(player);
        bossBar.setTitle(Tools.Text("&fNivel: &e" + level + " &7- &fExp: &e" + exp + "&7/" + needExp));        
    }
    
    public void removeBossbar() {
        if (bossBar.getPlayers().contains(player)) {
            bossBar.removePlayer(player);
        }
    }
    
}
