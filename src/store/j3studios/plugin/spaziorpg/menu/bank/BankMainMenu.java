package store.j3studios.plugin.spaziorpg.menu.bank;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import store.j3studios.plugin.spaziorpg.database.SQL;
import store.j3studios.plugin.spaziorpg.menu.Menu;
import store.j3studios.plugin.spaziorpg.utils.ItemBuilder;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class BankMainMenu extends Menu {

    public BankMainMenu(Player player) {
        super("Banco - Menu principal", 3);
                
        String account = SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_ACCOUNT, "uuid", player.getUniqueId().toString()).toString();
        Double balance = (double)SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, "uuid", player.getUniqueId().toString());
        Integer level = (int)SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_LEVEL, "uuid", player.getUniqueId().toString());
        
        Double precioOro = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE);
        Integer goldBank = (int)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_BANK_GOLDS);
        
        set(11, ItemBuilder.crearItem(Material.BOOK, 1, "&eCuenta", 
                "&7Información bancaria.",
                "",
                "&e» Estado:",
                "&fN° Cuenta: &e" + account,
                "&fBalance: &6&l$&e" + Tools.get().decimalFormat(balance, "###,###,###,###,###,###.##"),
                "&fNivel: &e" + level,
                "",
                "&e» Opciones:",
                "&f- Depósito.",
                "&f- Retiro.",
                "&f- Transferencia.",
                "&f- Historial.",
                "&f- Bonos.",
                "",
                "&a» Click para abrir el menu."));
        
        set(13, ItemBuilder.crearItem(Material.GOLD_INGOT, 1, "&eOro", 
                "&7Compra/Vende ORO",
                "&7Una moneda con vida propia!",
                "",
                "&e» Información:",
                "&fCantidad disponible: &e"+Tools.get().decimalFormat(goldBank, "###,###,###,###,###,###.##"),
                "",
                "&e» Valores:",
                "&fPrecio de compra: &2&l$&a"+Tools.get().decimalFormat(precioOro, "###,###,###,###,###,###.##"),
                "&fPrecio de venta: &6&l$&e"+Tools.get().decimalFormat(precioOro-500.0, "###,###,###,###,###,###.##"),
                "",
                "&e» Opciones:",
                "&f- Comprar oro.",
                "&f- Vender oro.",
                "&f- Historial de precios.",
                "",
                "&a» Click para abrir el menu."));
        
        set(15, ItemBuilder.crearItem(Material.PAPER, 1, "&eAcciones", 
                "&7Únete al equipo de socios del banco",
                "&7y obten ingresos de comisiones!",
                "",
                "&e» Información:",
                "&fAcciones disponibles: &e100",
                "",
                "&e» Valores:",
                "&fPrecio de compra: &2&l$&a750.000",
                "&fPrecio de venta: &6&l$&e745.000",
                "",
                "&e» Opciones:",
                "&f- Comprar acciones.",
                "&f- Vender acciones.",
                "",
                "&a» Click para abrir el menu."));
        
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        String name = Tools.Text(event.getCurrentItem().getItemMeta().getDisplayName());
	
        if (name.equalsIgnoreCase(Tools.Text("&eCuenta"))) {
            new BankAccountMenu(player).open(player);
        }
        
        if (name.equalsIgnoreCase(Tools.Text("&eOro"))) {
            new BankGoldMainMenu(player).open(player);
        }
        
        if (name.equalsIgnoreCase(Tools.Text("&eAcciones"))) {
            
        }
    }
        
    @Override
    public void onClose(Player player) {
    
    }
    
}
