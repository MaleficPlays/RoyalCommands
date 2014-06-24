package org.royaldev.royalcommands.rcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalcommands.MessageColor;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;

@ReflectCommand
public class CmdTeleportPlayerToPlayer implements CommandExecutor {

    private final RoyalCommands plugin;

    public CmdTeleportPlayerToPlayer(RoyalCommands instance) {
        plugin = instance;
    }


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("teleportplayertoplayer")) {
            if (!this.plugin.ah.isAuthorized(cs, cmd)) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 2) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            Player t1 = plugin.getServer().getPlayer(args[0]);
            Player t2 = plugin.getServer().getPlayer(args[1]);
            if (t1 == null || t2 == null || plugin.isVanished(t1, cs) || plugin.isVanished(t2, cs)) {
                cs.sendMessage(MessageColor.NEGATIVE + "That player does not exist!");
                return true;
            }
            if (!RUtils.isTeleportAllowed(t1) && !this.plugin.ah.isAuthorized(cs, cmd)) {
                cs.sendMessage(MessageColor.NEGATIVE + "The player " + MessageColor.NEUTRAL + t1.getName() + MessageColor.NEGATIVE + " has teleportation off!");
                return true;
            }
            if (!RUtils.isTeleportAllowed(t2) && !this.plugin.ah.isAuthorized(cs, cmd)) {
                cs.sendMessage(MessageColor.NEGATIVE + "The player " + MessageColor.NEUTRAL + t2.getName() + MessageColor.NEGATIVE + " has teleportation off!");
                return true;
            }
            String error = RUtils.teleport(t1, t2);
            if (!error.isEmpty()) {
                cs.sendMessage(MessageColor.NEGATIVE + error);
                return true;
            }
            cs.sendMessage(MessageColor.POSITIVE + "You have teleported " + MessageColor.NEUTRAL + t1.getName() + MessageColor.POSITIVE + " to " + MessageColor.NEUTRAL + t2.getName() + MessageColor.POSITIVE + ".");
            return true;
        }
        return false;
    }
}
