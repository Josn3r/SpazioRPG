package store.j3studios.plugin.spaziorpg.player;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.RPG;
import store.j3studios.plugin.spaziorpg.control.Level;
import store.j3studios.plugin.spaziorpg.utils.Config;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class SPlayer {
    
    private final UUID uuid;
    private final Player player;
    
    private Integer level = 1;
    private Integer exp = 0;
    private Integer totalExp = 0;
    
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
    
    /*
        LEVEL SYSTEM
    */

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public Integer getTotalExp() {
        return totalExp;
    }

    public void setTotalExp(Integer totalExp) {
        this.totalExp = totalExp;
    }
       
    public BossBar getBossbar() {
        return bossBar;
    }
    
    public void createBossbar() {
        Integer needExp = Level.get().getNeededExp(player);        
        bossBar.setColor(BarColor.YELLOW);
        bossBar.setStyle(BarStyle.SOLID);
        bossBar.setTitle(Tools.Text("&fNivel: &e" + level + " &7- &fExp: &e" + exp + "&7/" + needExp));
        bossBar.setVisible(true);
        Double calculo = exp / (double)needExp;        
        bossBar.setProgress(calculo);        
        bossBar.addPlayer(player);
    }
    
    public void updateBossbar() {
        if (!bossBar.getPlayers().contains(player)) {
            createBossbar();
            return;
        }
        Integer needExp = Level.get().getNeededExp(player);
        Double calculo = exp / (double)needExp;        
        bossBar.setTitle(Tools.Text("&fNivel: &e" + level + " &7- &fExp: &e" + exp + "&7/" + needExp));       
        bossBar.setProgress(calculo);
    }
    
    public void removeBossbar() {
        if (bossBar.getPlayers().contains(player)) {
            bossBar.removePlayer(player);
        }
    }
    
}
