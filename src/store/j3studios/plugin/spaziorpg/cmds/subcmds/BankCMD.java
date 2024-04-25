package store.j3studios.plugin.spaziorpg.cmds.subcmds;

import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.cmds.SubCommands;
import store.j3studios.plugin.spaziorpg.control.Banco;
import store.j3studios.plugin.spaziorpg.menu.bank.BankMainMenu;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class BankCMD extends SubCommands {

    @Override
    public String getName() {
        return "bank";
    }

    @Override
    public String getPermission() {
        return "spaziorpg.command.bank";
    }

    @Override
    public String getDescription() {
        return "Bank commands";
    }

    @Override
    public String getSyntax() {
        return "/spaziorpg bank ...";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 1) {
            player.sendMessage(Tools.Text(Tools.PREFIX + "&7Uso correcto: &f/spaziorpg bank open"));
            player.sendMessage(Tools.Text(Tools.PREFIX + "&7Uso correcto: &f/spaziorpg bank admin"));
        } else {
            if (args[1].equalsIgnoreCase("open")) {
                new BankMainMenu(player).open(player);
                return;
            }
            if (args[1].equalsIgnoreCase("admin")) {
                if (Banco.get().getPaydayLog().containsKey(player)) {
                    Banco.get().getPaydayLog().remove(player);
                } else {
                    Banco.get().getPaydayLog().put(player, 10000.0);
                }
            }
        }
    }
    
}
