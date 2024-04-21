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
import store.j3studios.plugin.spaziorpg.menu.Menu;
import store.j3studios.plugin.spaziorpg.utils.ItemBuilder;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class BankAccountTransferMenu extends Menu {

    public String account = "";
    public Double amount = 0.0;

    public BankAccountTransferMenu(Player player, String account, Double amount) {
        super("Banco - Transferencia", 4);
        
            this.account = account;
            this.amount = amount;
            Double calcComision = Tools.get().calcPercent(amount, Banco.get().getCommission(Banco.CommissionType.TRANSFER_NEGATIVE));
            Object playerName = SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_USERNAME, "account", account);
                
            set(12, ItemBuilder.crearItem(Material.LIME_DYE, 1, "&eN° de Cuenta", 
                    "&6» &f" + account,
                    "",
                    "&a» Click para modificar."));

            set(14, ItemBuilder.crearItem(Material.PAPER, 1, "&eMonto de transferencia", 
                    "&6» Monto: &f$" + Tools.get().decimalFormat(amount, "###,###,###,###,###,###.##"),
                    "&6» Comision: &f$" + Tools.get().decimalFormat(calcComision, "###,###,###,###,###,###.##"),
                    "",
                    "&a» Click para transferir."));

            set(22, ItemBuilder.crearItem(Material.CHEST, 1, "&aConfirmar", 
                    "&7Confirmar transferencia.",
                    "",
                    "&e» Información:",
                    "&f- N° Cuenta: &b" + account,
                    "&f- Usuario: &b" + (playerName != null ? playerName : "No encontrado"),
                    "&f- Monto: &b$" + Tools.get().decimalFormat(amount, "###,###,###,###,###,###.##"),
                    "&f- Comision: &b$" + Tools.get().decimalFormat(calcComision, "###,###,###,###,###,###.##"),
                    "",
                    "&a» Click para confirmar."));        
    }
    
    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        String name = Tools.Text(event.getCurrentItem().getItemMeta().getDisplayName());
	
        if (name.equalsIgnoreCase(Tools.Text("&eN° de Cuenta"))) {
            this.openAnvilMenu(player, "account");
        }
        
        if (name.equalsIgnoreCase(Tools.Text("&eMonto de transferencia"))) {
            this.openAnvilMenu(player, "amount");
        }
        
        if (name.equalsIgnoreCase(Tools.Text("&aConfirmar"))) {
            Banco.get().createTransferencia(player, account, amount);
            player.closeInventory();
        }        
        
    }
        
    @Override
    public void onClose(Player player) {
    
    }
    
    /*
        ANVIL GUI
    */
    
    public void openAnvilMenu(Player p, String type) {
        Double bBalance = (double)SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, "uuid", p.getUniqueId().toString());
        String money = "";
        
        if (type.equalsIgnoreCase("amount")) {
            money = Tools.get().decimalFormat(bBalance, "###,###,###,###,###,###.##");
        }
        
        new AnvilGUI.Builder().onClose(e -> {
            RPG.get().getServer().getScheduler().runTask(RPG.get(), () -> {
                new BankAccountTransferMenu(p, this.account, this.amount).open(p);
            });
        }).onClick((slot, e) -> {
            if(slot != AnvilGUI.Slot.OUTPUT) {
                return Collections.emptyList();
            }
            
            String text = e.getText();
            if (text.startsWith(" ")) {
                text = text.substring(1, text.length());
            } 
            
            if (type.equalsIgnoreCase("amount")) { 
                Pattern pattern = Pattern.compile("[0-9].*");
                Matcher match = pattern.matcher(text);
                if (!match.matches() && !Tools.get().isDouble(text)) {
                    return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Solo valor monetario"));
                }                
                this.amount = Double.valueOf(text);            
            } else {
                this.account = text;
            }                 
            Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            new BankAccountTransferMenu(p, this.account, this.amount).open(p);
            return Arrays.asList(AnvilGUI.ResponseAction.close());           
        }).text(" ")
          .itemLeft(new ItemStack(Material.PAPER))
          .title(Tools.Text(type.equalsIgnoreCase("amount") ? ("Dinero: &6$&e" + money) : "Numero de cuenta"))
          .plugin(RPG.get())
          .open(p);  
    }
    
}
