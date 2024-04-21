package store.j3studios.plugin.spaziorpg.menu.bank;

import java.util.ArrayList;
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
import store.j3studios.plugin.spaziorpg.utils.Vault;

public class BankAccountMenu extends Menu {

    public ArrayList<String> history = new ArrayList<>();
    
    public BankAccountMenu(Player player) {
        super("Banco - Cuenta", 4);
              
        loadHistory(player);
        
        set(11, ItemBuilder.crearItem(Material.LIME_DYE, 1, "&eDepositar", 
                "&7Realiza un depósito bancario.",
                "",
                "&e» Comisión: &f" + Tools.get().decimalFormat(Banco.get().getCommission(Banco.CommissionType.DEPOSIT), "##.##") + "%",
                "",
                "&a» Click para depositar."));
        
        set(13, ItemBuilder.crearItem(Material.PAPER, 1, "&eTransferencia", 
                "&7Realiza una transferencia bancaria.",
                "",
                "&e» Comisión: &f" + Tools.get().decimalFormat(Banco.get().getCommission(Banco.CommissionType.TRANSFER_NEGATIVE), "##.##") + "%",
                "",
                "&a» Click para transferir."));
        
        set(15, ItemBuilder.crearItem(Material.RED_DYE, 1, "&eRetirar", 
                "&7Realiza un retiro bancario.",
                "",
                "&e» Comisión: &f" + Tools.get().decimalFormat(Banco.get().getCommission(Banco.CommissionType.WITHDRAW), "##.##") + "%",
                "",
                "&a» Click para retirar."));
        
        set(21, ItemBuilder.crearItem(Material.CHEST, 1, "&eBonos", 
                "&7Reclama un bono otorgado por el banco.",
                "",
                "&e» Bonos disponibles: &f0",
                "",
                "&a» Click para abrir."));
        
        set(23, ItemBuilder.crearItem(Material.BOOK, 1, "&eHistorial", history));
        
    }

    private void loadHistory(Player player) {
        history.add(Tools.Text("&7Historial de movimientos."));
        history.add(Tools.Text(" "));
        history.add(Tools.Text("&e» Historial:"));
        for (String str : Banco.get().getAccountTransactions(player)) {
            history.add(Tools.Text(str));
        }
    }
    
    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        String name = Tools.Text(event.getCurrentItem().getItemMeta().getDisplayName());
	
        if (name.equalsIgnoreCase(Tools.Text("&eDepositar"))) {
            this.openAnvilMenu(player, "deposit");
        }
        
        if (name.equalsIgnoreCase(Tools.Text("&eTransferencia"))) {
            new BankAccountTransferMenu(player, "¿-?", 0.0).open(player);
        }
        
        if (name.equalsIgnoreCase(Tools.Text("&eRetirar"))) {
            this.openAnvilMenu(player, "withdraw");
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
        
        if (type.equalsIgnoreCase("deposit")) {
            money = Tools.get().decimalFormat(Vault.getMoney(p), "###,###,###,###,###,###.##");
        } else {
            money = Tools.get().decimalFormat(bBalance, "###,###,###,###,###,###.##");
        }
        
        new AnvilGUI.Builder().onClose(e -> {
            RPG.get().getServer().getScheduler().runTask(RPG.get(), () -> {
                new BankAccountMenu(p).open(p);
            });
        }).onClick((slot, e) -> {
            if(slot != AnvilGUI.Slot.OUTPUT) {
                return Collections.emptyList();
            }
            String text = e.getText();
            if (text.startsWith(" ")) {
                text = text.substring(1, text.length());
            } 
            Pattern pattern = Pattern.compile("[0-9].*");
            Matcher match = pattern.matcher(text);

            if (!match.matches() && !Tools.get().isDouble(text)) {
                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Solo valor monetario"));
            }
            
            Double value = Double.valueOf(text);
            if (type.equalsIgnoreCase("deposit")) {              
                if (Vault.getMoney(p) < value) {
                    return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Balance insuficiente"));
                }
                Vault.removeMoney(p, value);                
                RPG.get().getServer().getScheduler().runTask(RPG.get(), () -> {
                    Banco.get().createTransaction(p.getUniqueId().toString(), Banco.TransactionType.DEPOSIT, value, "");                    
                });
            } else {
                Double comission = Banco.get().getCommission(Banco.CommissionType.WITHDRAW);
                Double calcCommission = Tools.get().calcPercent(Double.valueOf(text), comission);                
                if (bBalance < value) {
                    return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Balance insuficiente"));
                }                
                Vault.setMoney(p, (value-calcCommission));                
                RPG.get().getServer().getScheduler().runTask(RPG.get(), () -> {
                    Banco.get().createTransaction(p.getUniqueId().toString(), Banco.TransactionType.WITHDRAW, value, "");                  
                });
            }         
            
            Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            new BankAccountMenu(p).open(p);
            return Arrays.asList(AnvilGUI.ResponseAction.close());           
        }).text(" ")
          .itemLeft(new ItemStack(Material.PAPER))
          .title(Tools.Text("Dinero: &6$&e" + money))
          .plugin(RPG.get())
          .open(p);  
    }
    
}
