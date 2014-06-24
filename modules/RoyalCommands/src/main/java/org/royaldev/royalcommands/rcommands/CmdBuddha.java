package org.royaldev.royalcommands.rcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalcommands.AuthorizationHandler.PermType;
import org.royaldev.royalcommands.MessageColor;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;
import org.royaldev.royalcommands.configuration.PConfManager;

@ReflectCommand
public class CmdBuddha implements CommandExecutor {

    private final RoyalCommands plugin;

    public CmdBuddha(RoyalCommands instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("buddha")) {
            if (!this.plugin.ah.isAuthorized(cs, cmd)) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length > 0) {
                if (!this.plugin.ah.isAuthorized(cs, cmd, PermType.OTHERS)) {
                    RUtils.dispNoPerms(cs);
                    return true;
                }
                Player t = plugin.getServer().getPlayer(args[0]);
                if (t == null || plugin.isVanished(t, cs)) {
                    cs.sendMessage(MessageColor.NEGATIVE + "That player does not exist!");
                    return true;
                }
                PConfManager pcm = PConfManager.getPConfManager(t);
                if (!pcm.getBoolean("buddha")) {
                    pcm.set("buddha", true);
                    t.sendMessage(MessageColor.POSITIVE + "Buddha mode enabled by " + MessageColor.NEUTRAL + cs.getName() + MessageColor.POSITIVE + ".");
                    cs.sendMessage(MessageColor.POSITIVE + "Enabled buddha mode for " + MessageColor.NEUTRAL + t.getName() + MessageColor.POSITIVE + ".");
                } else {
                    pcm.set("buddha", false);
                    t.sendMessage(MessageColor.POSITIVE + "Buddha mode disabled by " + MessageColor.NEUTRAL + cs.getName() + MessageColor.POSITIVE + ".");
                    cs.sendMessage(MessageColor.POSITIVE + "Disabled buddha mode for " + MessageColor.NEUTRAL + t.getName() + MessageColor.POSITIVE + ".");
                }
                return true;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(MessageColor.NEGATIVE + "This command is only available to players!");
                return true;
            }
            Player p = (Player) cs;
            PConfManager pcm = PConfManager.getPConfManager(p);
            if (!pcm.getBoolean("buddha")) {
                pcm.set("buddha", true);
                cs.sendMessage(MessageColor.POSITIVE + "Enabled buddha mode for yourself.");
            } else {
                pcm.set("buddha", false);
                cs.sendMessage(MessageColor.POSITIVE + "Disabled buddha mode for yourself.");
            }
            return true;
        }
        return false;
    }

}
