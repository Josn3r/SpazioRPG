package store.j3studios.plugin.spaziorpg.menu.bank;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import store.j3studios.plugin.spaziorpg.database.SQL;
import store.j3studios.plugin.spaziorpg.menu.Menu;
import store.j3studios.plugin.spaziorpg.utils.ItemBuilder;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class BankGoldHistoryMenu extends Menu {

    public BankGoldHistoryMenu(Player player) {
        super("Banco - Historial del Oro", 3);
        
            ArrayList<String> historial = SQL.get().getGoldHistory(24);
            
            set(0, ItemBuilder.crearItem(Material.RED_DYE, 1, "&cAtrás", "&7Regresa al menu anterior..."));

            Integer slot = 3;
            for (String str : historial) {
                String fecha = str.split(" / ")[0];
                String hora = str.split(" / ")[1];                
                Double price = Double.valueOf(str.split(" / ")[2]);                
                Double last_price = Double.valueOf(str.split(" / ")[3]);                
                Double diff_price = Double.valueOf(str.split(" / ")[4]);                
                Double diff_percent = Double.valueOf(str.split(" / ")[5]);                
                 
                boolean aumento = false;
                if (price > last_price) {
                    aumento = true;
                }
                
                String diffStr = "";
                if (aumento) {
                    diffStr = Tools.Text("&a+" + Tools.get().decimalFormat(diff_percent, "###,###.##"));
                } else {
                    diffStr = Tools.Text("&c" + Tools.get().decimalFormat(diff_percent, "###,###.##"));
                }
                        
                set(slot, ItemBuilder.crearItem(Material.GOLD_NUGGET, 1, "&e", 
                    "&f» Fecha: &e"+fecha,
                    "&f» Hora: &e"+hora+" Hr",
                    "",
                    "&f» Precio de compra: &6&l$&e" + Tools.get().decimalFormat(price, "###,###,###,###,###,###.##") + " &7(" + diffStr + "&7)",
                    "&f» Precio de venta: &6&l$&e" + Tools.get().decimalFormat(price-500.0, "###,###,###,###,###,###.##") + " &7(" + diffStr + "&7)",
                    ""));
                ++slot;
            }     
    }
    
    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        String name = Tools.Text(event.getCurrentItem().getItemMeta().getDisplayName());
	
        if (name.equalsIgnoreCase(Tools.Text("&cAtrás"))) {
            new BankGoldMainMenu(player).open(player);
        }        
        
    }
        
    @Override
    public void onClose(Player player) {
    
    }
    
}
