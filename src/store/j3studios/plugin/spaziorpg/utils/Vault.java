package store.j3studios.plugin.spaziorpg.utils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import store.j3studios.plugin.spaziorpg.RPG;

@SuppressWarnings("deprecation")
public class Vault {

    public static double getMoney(Player p) { return RPG.econ.getBalance(p.getName()); }
    public static void setMoney(Player p, double money){ RPG.econ.depositPlayer(p.getName(), money); }
    public static void setMoney(String player, double money){ RPG.econ.depositPlayer(player, money); }
    public static void removeMoney(Player p, double money) { RPG.econ.withdrawPlayer(p.getName(), money); }
		
    public static boolean setupEconomy() {
        if (RPG.get().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = RPG.get().getServer().getServicesManager().getRegistration(Economy.class);
	if (rsp == null) {
            return false;
        }
        RPG.econ = ((Economy)rsp.getProvider());
        return RPG.econ != null;
    }

    public static void setMoney(OfflinePlayer offlinePlayer, double totalEarnings) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}