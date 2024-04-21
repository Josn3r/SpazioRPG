package store.j3studios.plugin.spaziorpg.menu.bank;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import store.j3studios.plugin.spaziorpg.control.Banco;
import store.j3studios.plugin.spaziorpg.database.SQL;
import store.j3studios.plugin.spaziorpg.menu.Menu;
import store.j3studios.plugin.spaziorpg.utils.ItemBuilder;
import store.j3studios.plugin.spaziorpg.utils.Tools;
import store.j3studios.plugin.spaziorpg.utils.Vault;

public class BankGoldMainMenu extends Menu {

    public BankGoldMainMenu(Player player) {
        super("Banco - Oro", 4);
        
            Double precioOro = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE);
            Integer goldBank = (int)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_BANK_GOLDS);
            Integer goldPlayer = (int)SQL.get().getData(SQL.DataType.PLAYER_GOLDS, SQL.UpdateType.PLAYER_GOLDS_GOLDS, "uuid", player.getUniqueId().toString());
        
            set(12, ItemBuilder.crearItem(Material.LIME_DYE, 1, "&eComprar Oro", 
                    "",
                    "&f» Cantidad disponible: " + Tools.get().decimalFormat(goldBank, "###,###,###,###,###,###"),
                    "&f» Precio: &6&l$&e" + Tools.get().decimalFormat(precioOro, "###,###,###,###,###,###.##"),
                    "",
                    "&e» Click para comprar."));

            set(14, ItemBuilder.crearItem(Material.RED_DYE, 1, "&eVender Oro", 
                    "",
                    "&f» Cantidad en billetera: " + Tools.get().decimalFormat(goldPlayer, "###,###,###,###,###,###"),
                    "&f» Precio: &6&l$&e" + Tools.get().decimalFormat(precioOro-500.0, "###,###,###,###,###,###.##"),
                    "",
                    "&e» Click para vender."));

            set(22, ItemBuilder.crearItem(Material.CHEST, 1, "&eHistorial", 
                    "&7Revisa el historia de las ultimas",
                    "&724 horas del precio del oro.",
                    "",
                    "&e» Click para abrir el historial."));        
    }
    
    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        String name = Tools.Text(event.getCurrentItem().getItemMeta().getDisplayName());
	
        if (name.equalsIgnoreCase(Tools.Text("&eComprar Oro"))) {
            Integer cantidad = 100;
            Double precio = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE);    
            Integer goldBank = (int)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_BANK_GOLDS);
            Double valor = precio * cantidad;
            if (goldBank < cantidad) {
                player.sendMessage(Tools.Text(Tools.PREFIX + "&cEl banco no tiene suficiente stock."));
                player.closeInventory();
                return;
            }
            if (Vault.getMoney(player) < valor) {     
                player.sendMessage(Tools.Text(Tools.PREFIX + "&cNo tienes suficiente dinero..."));
                player.closeInventory();
                return;
            }
            Vault.removeMoney(player, valor);
            Banco.get().comprarOro(player.getUniqueId().toString(), cantidad);
            player.sendMessage(Tools.Text(Tools.PREFIX + "&fCompraste &e"+cantidad+" de Oro &fal banco por &6&l$&e" + Tools.get().decimalFormat(valor, "###,###,###,###,###,###.##")));
            player.closeInventory();            
        }
        
        if (name.equalsIgnoreCase(Tools.Text("&eVender Oro"))) {
            Integer cantidad = 100;
            Double precio = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE)-500.0;    
            Integer goldPlayer = (int)SQL.get().getData(SQL.DataType.PLAYER_GOLDS, SQL.UpdateType.PLAYER_GOLDS_GOLDS, "uuid", player.getUniqueId().toString());        
            Double valor = precio * cantidad;  
            if (goldPlayer < cantidad) {
                player.sendMessage(Tools.Text(Tools.PREFIX + "&cNo tienes esa cantidad de oro."));
                player.closeInventory();
                return;
            }
            Vault.setMoney(player, valor);
            Banco.get().venderOro(player.getUniqueId().toString(), cantidad);
            player.sendMessage(Tools.Text(Tools.PREFIX + "&fVendiste &e"+cantidad+" de Oro &fal banco por &6&l$&e" + Tools.get().decimalFormat(valor, "###,###,###,###,###,###.##")));
            player.closeInventory();            
        }
        
        if (name.equalsIgnoreCase(Tools.Text("&eHistorial"))) {
            player.closeInventory();
        }        
        
    }
        
    @Override
    public void onClose(Player player) {
    
    }
    
    /*
        ANVIL GUI
    */
    
    
}
