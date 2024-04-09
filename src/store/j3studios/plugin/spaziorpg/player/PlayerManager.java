package store.j3studios.plugin.spaziorpg.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;

public class PlayerManager {
    
    private static PlayerManager ins;
    public static PlayerManager get() {
        if (ins == null) {
            ins = new PlayerManager();
        }
        return ins;
    }
    
    private final Map<UUID, SPlayer> players = new HashMap<>();
    
    public Map<UUID, SPlayer> getPlayers() {
        return players;
    }
    
    public SPlayer getPlayer(UUID uuid) {
        return this.players.get(uuid);
    }
    
    public Boolean isPlayerExists(UUID uuid) {
        return this.players.containsKey(uuid);
    }
    
    public void createPlayer (Player player) {
        this.players.put(player.getUniqueId(), new SPlayer(player.getUniqueId()));
    }
    
    public void removePlayer (Player player) {
        this.players.remove(player.getUniqueId());
    }
    
    public Set<SPlayer> PlayersSet(Set<UUID> set) {
        HashSet<SPlayer> hashSet = new HashSet<>();
        for (UUID uuid : set) {
            hashSet.add(this.getPlayer(uuid));
        }
        return hashSet;
    }
    
    public Set<SPlayer> PlayersSet() {
        HashSet<SPlayer> hashSet = new HashSet<>();
        for (UUID uuid : getPlayers().keySet()) {
            hashSet.add(this.getPlayer(uuid));
        }
        return hashSet;
    }
    
}
