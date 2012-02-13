package org.royaldev.royalcommands.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;

public class CmdTppos implements CommandExecutor {

    RoyalCommands plugin;

    public CmdTppos(RoyalCommands instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tppos")) {
            if (!(cs instanceof Player)) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 2) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            Double x = RUtils.getDouble(args[0]);
            Double y = RUtils.getDouble(args[1]);
            Double z = RUtils.getDouble(args[2]);
            if (x == null || y == null || z == null) {
                cs.sendMessage(ChatColor.RED + "One of the coordinates was invalid.");
                return true;
            }
            Player p = (Player) cs;
            Location pLoc;
            World w;
            if (args.length > 2) {
                w = plugin.getServer().getWorld(args[3]);
                if (w == null) {
                    cs.sendMessage(ChatColor.RED + "That world does not exist!");
                    return true;
                }
                pLoc = new Location(w, x, y, z);
            } else {
                w = p.getWorld();
                pLoc = new Location(w, x, y, z);
            }
            cs.sendMessage(ChatColor.BLUE + "Teleporting you to x: " + ChatColor.GRAY + x + ChatColor.BLUE + ", y: " + ChatColor.GRAY + y + ChatColor.BLUE + ", z: " + ChatColor.GRAY + z + ChatColor.BLUE + " in world " + ChatColor.GRAY + w.getName() + ChatColor.BLUE + ".");
            p.teleport(pLoc);
        }
        return false;
    }

}
