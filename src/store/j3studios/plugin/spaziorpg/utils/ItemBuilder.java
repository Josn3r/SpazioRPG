package store.j3studios.plugin.spaziorpg.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder {
    
    @SuppressWarnings("deprecation")
    public static ItemStack crearCabeza(String owner, String name, String ... lore) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta)item.getItemMeta();
        meta.setOwner(owner);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        ArrayList<String> color = new ArrayList<>();
        for (String b : lore) {
            color.add(ChatColor.translateAlternateColorCodes((char)'&', (String)b));
            meta.setLore(color);
        }
        item.setItemMeta((ItemMeta)meta);
        return item;
    }

    @SuppressWarnings("deprecation")
	public static ItemStack crearCabeza(String owner, String name, Integer i, String ... lore) {
    	ItemStack item = new ItemStack(Material.PLAYER_HEAD, i);
        SkullMeta meta = (SkullMeta)item.getItemMeta();
        meta.setOwner(owner);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        ArrayList<String> color = new ArrayList<>();
        for (String b : lore) {
            color.add(ChatColor.translateAlternateColorCodes((char)'&', (String)b));
            meta.setLore(color);
        }
        item.setItemMeta((ItemMeta)meta);
        return item;
    }

    @SuppressWarnings("deprecation")
	public static ItemStack crearCabeza(String owner, String name, List<String> lore) {        
    	ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta)item.getItemMeta();
        meta.setOwner(owner);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        ArrayList<String> color = new ArrayList<>();
        for (String b : lore) {
            color.add(ChatColor.translateAlternateColorCodes((char)'&', (String)b));
            meta.setLore(color);
        }
        item.setItemMeta((ItemMeta)meta);
        return item;
    }

	public static ItemStack crearItem(Material mat, int amount) {
    	ItemStack item = new ItemStack(mat, amount);
        return item;
    }
    
    public static ItemStack crearItem(Material mat, int amount, String name, String ... lore) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        ArrayList<String> color = new ArrayList<>();
        for (String b : lore) {
            color.add(ChatColor.translateAlternateColorCodes((char)'&', (String)b));
            meta.setLore(color);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack crearItem(Material mat, int amount, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        ArrayList<String> color = new ArrayList<>();
        for (String b : lore) {
            color.add(ChatColor.translateAlternateColorCodes((char)'&', (String)b));
            meta.setLore(color);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack crearItem(Material mat, int amount, boolean enchant, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        ArrayList<String> color = new ArrayList<>();
        for (String b : lore) {
            color.add(ChatColor.translateAlternateColorCodes((char)'&', (String)b));
            meta.setLore(color);
        }
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        return item;
    }
    
    public static String capitalize(ItemStack itemStack) {
        String[] name = itemStack.getType().name().toLowerCase().replace("_spawn_egg", "").split("_");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length; i++)
            builder.append((i == 0) ? (name[i].substring(0, 1).toUpperCase() + name[i].substring(1)) : (" " + name[i].substring(0, 1).toUpperCase() + name[i].substring(1))); 
        return builder.toString();
    }
    
}

