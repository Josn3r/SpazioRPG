package store.j3studios.plugin.spaziorpg.player;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.RPG;
import store.j3studios.plugin.spaziorpg.utils.Config;

public class SPlayer {
    
    private final UUID uuid;
    private final Player player;
    
    private Config lang;
    
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
    
    public Config getLang() {
        return lang;
    }    
    
}
