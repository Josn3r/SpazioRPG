package store.j3studios.plugin.spaziorpg.cmds;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.cmds.subcmds.BankCMD;
import store.j3studios.plugin.spaziorpg.cmds.subcmds.GoldsCMD;
import store.j3studios.plugin.spaziorpg.cmds.subcmds.LevelCMD;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class CommandManager implements TabExecutor {

    private final ArrayList<SubCommands> subCommands = new ArrayList<>();
    
    public CommandManager() {
        subCommands.add(new LevelCMD());
        subCommands.add(new BankCMD());
        subCommands.add(new GoldsCMD());
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args) {
        if (args.length == 1) {
            ArrayList<String> cmds = new ArrayList<>();
            for (int i = 0; i < this.getSubCommands().size(); ++i) {
                cmds.add(this.getSubCommands().get(i).getName());
            }
            return cmds;
        }
        if (args[0].equalsIgnoreCase("level")) {
            ArrayList<String> cmds = new ArrayList<>();
            if (args.length == 2) {
                cmds.add("giveexp");
                cmds.add("reset");
            }
            if (args.length == 3) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    cmds.add(online.getName());
                }
            }
            return cmds;
        }
        if (args[0].equalsIgnoreCase("bank")) {
            ArrayList<String> cmds = new ArrayList<>();
            if (args.length == 2) {
                cmds.add("open");
                cmds.add("admin");
            }
            return cmds;
        }
        if (args[0].equalsIgnoreCase("golds")) {
            ArrayList<String> cmds = new ArrayList<>();
            if (args.length == 2) {
                cmds.add("start");
                cmds.add("admin");
            }
            return cmds;
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
        if (sender instanceof Player p) {
            if (args.length == 0) {
                // help cmd
            } else if (args.length > 0) {
                for (int i = 0; i < this.getSubCommands().size(); ++i) {
                    if (args[0].equalsIgnoreCase(this.getSubCommands().get(i).getName())) {
                        if (!p.hasPermission(this.getSubCommands().get(i).getPermission())) {
                            sender.sendMessage(Tools.Text(Tools.PREFIX + "&cNo tienes permiso para usar este comando."));
                            return true;
                        }
                        this.getSubCommands().get(i).perform(p, args);
                        return true;
                    }
                }
            }
        }
        return true;
    }

    public ArrayList<SubCommands> getSubCommands() {
        return subCommands;
    }    
    
    
}
