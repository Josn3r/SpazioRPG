package store.j3studios.plugin.spaziorpg.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.RPG;
import store.j3studios.plugin.spaziorpg.control.Banco;
import store.j3studios.plugin.spaziorpg.database.SQL;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class PAPIHook extends PlaceholderExpansion {
	
    private final RPG plugin;    
    public PAPIHook(RPG plugin) { this.plugin = plugin; }	
    
    @Override
    public boolean persist() { return true; }    
    @Override
    public boolean canRegister() { return true; }	
    
    @Override
    public String getAuthor() { return "Josn3r"; }    
    @Override
    public String getIdentifier() { return "spaziorpg"; }
    @Override
    public String getVersion() { return this.plugin.getDescription().getVersion(); }
    
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null)
            return "";             
        if (identifier.equals("bank_account")) {
            String account = SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_ACCOUNT, "uuid", player.getUniqueId().toString()).toString();
            return account;
        }
        if (identifier.equals("bank_balance")) {
            Double balance = (double)SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, "uuid", player.getUniqueId().toString());
            return Tools.get().decimalFormat(balance, "###,###,###,###,###,###,###,###.##");
        }
        if (identifier.equals("bank_level")) {
            Integer level = (int)SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_LEVEL, "uuid", player.getUniqueId().toString());
            return ""+level;
        }
        if (identifier.equals("has_interest")) {
            if (Banco.get().getPaydayLog().containsKey(player)){
                return "yes";
            }
            return "no";
        }
        if (identifier.equals("claim_interest")) {
            if (Banco.get().getPaydayLog().containsKey(player)) {
                return Tools.Text("&aTienes un cheque pendiente!");
            }
            return Tools.Text("&cNo tienes pagos pendientes!");
        } 
        return null;
    }
}
