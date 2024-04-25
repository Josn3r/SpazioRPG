package store.j3studios.plugin.spaziorpg.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import store.j3studios.plugin.spaziorpg.RPG;
import store.j3studios.plugin.spaziorpg.player.PlayerManager;
import store.j3studios.plugin.spaziorpg.player.SPlayer;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public abstract class MenuIA implements Listener {
    
    TexturedInventoryWrapper _inv;
    
    public MenuIA (String menuName, Integer rows, String fontImage) {
        _inv = new TexturedInventoryWrapper(null, (9 * rows), Tools.Text(menuName), new FontImageWrapper(fontImage));
        RPG.get().getServer().getPluginManager().registerEvents(this, RPG.get());
    }
    
    public void add(ItemStack stack) {
        _inv.getInternal().addItem(new ItemStack[]{stack});
    }
    
    public void set(Integer slot, ItemStack stack) {
        _inv.getInternal().setItem(slot, stack);
    }
    
    public void clear() {
        _inv.getInternal().clear();
    }
    
    public Inventory getInventory() {
        return _inv.getInternal();
    } 
    
    public void open(Player player) {
        SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
        sp.setOpenMenu(true);
        _inv.showInventory(player);
    }
    
    @EventHandler
    public void onInventoryClickEvent (InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory()) && e.getCurrentItem() != null && getInventory().contains(e.getCurrentItem()) && e.getWhoClicked() instanceof Player) {
            e.setCancelled(true);
            e.getWhoClicked().setItemOnCursor(null);
            onClick((Player)e.getWhoClicked(), e);
        }
    }
    
    @EventHandler
    public void onInventoryCloseEvent (InventoryCloseEvent e) {
        if (e.getInventory().equals(getInventory()) && e.getPlayer() instanceof Player) {
            SPlayer sp = PlayerManager.get().getPlayer(e.getPlayer().getUniqueId());
            sp.setOpenMenu(false);
            onClose((Player)e.getPlayer());
        }
    }
    
    public abstract void onClose (Player player);
    public abstract void onClick (Player player, InventoryClickEvent event);
    
}
