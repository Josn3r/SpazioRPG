package store.j3studios.plugin.spaziorpg.cmds.subcmds;

import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.cmds.SubCommands;
import store.j3studios.plugin.spaziorpg.database.SQL;
import store.j3studios.plugin.spaziorpg.menu.bank.BankMainMenu;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class GoldsCMD extends SubCommands {

    @Override
    public String getName() {
        return "golds";
    }

    @Override
    public String getPermission() {
        return "spaziorpg.command.golds";
    }

    @Override
    public String getDescription() {
        return "Golds commands";
    }

    @Override
    public String getSyntax() {
        return "/spaziorpg golds ...";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 1) {
            player.sendMessage(Tools.Text(Tools.PREFIX + "&7Uso correcto: &f/spaziorpg golds start <start-golds-stock> <start-golds-price>"));
            player.sendMessage(Tools.Text(Tools.PREFIX + "&7Uso correcto: &f/spaziorpg golds admin"));
        } else {
            if (args[1].equalsIgnoreCase("start")) {
                if (!Tools.get().isInt(args[2])) { return; }
                if (!Tools.get().isDouble(args[3])) { return; }
                Integer amount = Integer.valueOf(args[2]);  
                Double price = Double.valueOf(args[3]);                
                SQL.get().startGoldStats(amount, price);
                player.sendMessage(Tools.Text(Tools.PREFIX + "&fSe inició las estadísticas del oro."));
                player.sendMessage(Tools.Text(Tools.PREFIX + "&fOro inicial: &e" + amount + " &7- &fPrecio: &6&l$&e" + Tools.get().decimalFormat(price, "###,###,###,###.##")));
            }
            if (args[1].equalsIgnoreCase("admin")) {
                player.sendMessage(Tools.Text(Tools.PREFIX + "&fAbriendo menú de control del oro."));
            }
        }
    }
    
}
