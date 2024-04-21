package store.j3studios.plugin.spaziorpg.cmds.subcmds;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.cmds.SubCommands;
import store.j3studios.plugin.spaziorpg.control.Level;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class LevelCMD extends SubCommands {

    @Override
    public String getName() {
        return "level";
    }
    
    @Override
    public String getPermission() {
        return "spaziorpg.command.level";
    }

    @Override
    public String getDescription() {
        return "Level commands";
    }

    @Override
    public String getSyntax() {
        return "/spaziorpg level ...";
    }

    @Override
    public void perform(Player player, String[] args) {
        
        if (args.length == 1) {
            player.sendMessage(Tools.Text(Tools.PREFIX + "&7Uso correcto: &f/spaziorpg level &egiveexp <player> <amount>"));
            player.sendMessage(Tools.Text(Tools.PREFIX + "&7Uso correcto: &f/spaziorpg level &ereset <player>"));
        } else {
            if (args[1].equalsIgnoreCase("giveexp")) {
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                   player.sendMessage(Tools.Text(Tools.PREFIX + "&cEl jugador &f" + args[2] + " &cno está conectado."));
                   return;
                }                
                Integer amount = Integer.valueOf(args[3]);                
                Level.get().giveExp(target, amount);
                return;
            }
            if (args[1].equalsIgnoreCase("reset")) {
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    player.sendMessage(Tools.Text(Tools.PREFIX + "&cEl jugador &f" + args[2] + " &cno está conectado."));
                    return;
                }
            }
        }
        
    }
    
    
    
}
