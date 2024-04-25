package store.j3studios.plugin.spaziorpg.menu.bank;

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import store.j3studios.plugin.spaziorpg.RPG;
import store.j3studios.plugin.spaziorpg.control.Banco;
import store.j3studios.plugin.spaziorpg.database.SQL;
import store.j3studios.plugin.spaziorpg.menu.MenuIA;
import store.j3studios.plugin.spaziorpg.utils.ItemBuilder;
import store.j3studios.plugin.spaziorpg.utils.Tools;
import store.j3studios.plugin.spaziorpg.utils.Vault;

public class BankGoldMainMenu extends MenuIA {

    public BankGoldMainMenu(Player player) {
        super("&f  &eBanco - Oro", 6, "spaziorpg:vanilla_empty_menu");
        
            Double precioOro = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE);
            Integer goldBank = (int)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_BANK_GOLDS);
            Integer goldPlayer = (int)SQL.get().getData(SQL.DataType.PLAYER_GOLDS, SQL.UpdateType.PLAYER_GOLDS_GOLDS, "uuid", player.getUniqueId().toString());
        
            set(12, ItemBuilder.crearItem(Material.LIME_DYE, 1, "&eComprar Oro", 
                    "",
                    "&f» Cantidad disponible: &e" + Tools.get().decimalFormat(goldBank, "###,###,###,###,###,###"),
                    "&f» Precio de compra: &6&l$&e" + Tools.get().decimalFormat(precioOro, "###,###,###,###,###,###.##"),
                    "",
                    "&e» Click para comprar."));

            set(14, ItemBuilder.crearItem(Material.RED_DYE, 1, "&eVender Oro", 
                    "",
                    "&f» Cantidad en billetera: &e" + Tools.get().decimalFormat(goldPlayer, "###,###,###,###,###,###"),
                    "&f» Precio de venta: &6&l$&e" + Tools.get().decimalFormat(precioOro-500.0, "###,###,###,###,###,###.##"),
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
            this.openAnvilMenu(player, "buy");   
        }
        
        if (name.equalsIgnoreCase(Tools.Text("&eVender Oro"))) {
            this.openAnvilMenu(player, "sell");        
        }
        
        if (name.equalsIgnoreCase(Tools.Text("&eHistorial"))) {
            new BankGoldHistoryMenu(player).open(player);
        }        
        
    }
        
    @Override
    public void onClose(Player player) {
    
    }
    
    /*
        ANVIL GUI
    */
    
    public void openAnvilMenu(Player p, String type) {
        Integer goldPlayer = (int)SQL.get().getData(SQL.DataType.PLAYER_GOLDS, SQL.UpdateType.PLAYER_GOLDS_GOLDS, "uuid", p.getUniqueId().toString());
        Integer goldBank = (int)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_BANK_GOLDS);
        Double precioOro = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE);
        String title = "";
        
        if (type.equalsIgnoreCase("buy")) {
            title = "&eCompra &7- &e" + Tools.get().decimalFormat(goldBank, "###,###,###,###,###,###") + " &7- &6&l$&e" + Tools.get().decimalFormat(precioOro, "###,###,###,###,###,###.##");
        } else {
            title = "&eVenta &7- &e" + Tools.get().decimalFormat(goldPlayer, "###,###,###,###,###,###") + " &7- &6&l$&e" + Tools.get().decimalFormat(precioOro-500.0, "###,###,###,###,###,###.##");;
        }
        
        new AnvilGUI.Builder().onClose(e -> {
            RPG.get().getServer().getScheduler().runTask(RPG.get(), () -> {
                new BankGoldMainMenu(p).open(p);
            });
        }).onClick((slot, e) -> {
            if(slot != AnvilGUI.Slot.OUTPUT) {
                return Collections.emptyList();
            }
            String text = e.getText();
            if (text.startsWith(" ")) {
                text = text.substring(1, text.length());
            } 
            
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher match = pattern.matcher(text);
            if (!match.matches() && !Tools.get().isInt(text)) {
                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Cantidad en numeros"));
            }            
            Integer cantidad = Integer.valueOf(text);
            
            if (type.equalsIgnoreCase("buy")) {
                Double valor = precioOro * cantidad; 
                if (goldBank < cantidad) {
                    return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("No hay suficiente stock"));
                }
                if (Vault.getMoney(p) < valor) {     
                    return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("No tienes suficiente dinero"));
                }
                Vault.removeMoney(p, valor);
                Banco.get().comprarOro(p.getUniqueId().toString(), cantidad);
                p.sendMessage(Tools.Text(Tools.PREFIX + "&fCompraste &e"+cantidad+" de Oro &fal banco por &6&l$&e" + Tools.get().decimalFormat(valor, "###,###,###,###,###,###.##")));
            } else {
                Double valor = (precioOro-500.0) * cantidad;  
                if (goldPlayer < cantidad) {
                    return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("No tienes esa cantidad de oro"));
                }
                Vault.setMoney(p, valor);
                Banco.get().venderOro(p.getUniqueId().toString(), cantidad);
                p.sendMessage(Tools.Text(Tools.PREFIX + "&fVendiste &e"+cantidad+" de Oro &fal banco por &6&l$&e" + Tools.get().decimalFormat(valor, "###,###,###,###,###,###.##")));
            }         
            
            Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            new BankGoldMainMenu(p).open(p);
            return Arrays.asList(AnvilGUI.ResponseAction.close());           
        }).text(" ")
          .itemLeft(new ItemStack(Material.PAPER))
          .title(Tools.Text(title))
          .plugin(RPG.get())
          .open(p);  
    }
    
}
