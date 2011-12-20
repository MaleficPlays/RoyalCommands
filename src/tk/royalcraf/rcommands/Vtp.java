package tk.royalcraf.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.royalcraf.royalcommands.RoyalCommands;

public class Vtp implements CommandExecutor {

	RoyalCommands plugin;

	public Vtp(RoyalCommands plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("vtp")) {
			if (!plugin.isAuthorized(cs, "rcmds.vtp")) {
				cs.sendMessage(ChatColor.RED
						+ "You don't have permission for that!");
				return true;
			} else {
				if (args.length < 1) {
					return false;
				}
				Player victim = plugin.getServer().getPlayer(args[0]);
				if (victim != null) {
					if (cs instanceof Player) {
						Player player = (Player) cs;
						cs.sendMessage(ChatColor.BLUE
								+ "Teleporting you to player " + ChatColor.GRAY
								+ victim.getName() + ChatColor.BLUE + ".");
						victim.sendMessage(ChatColor.GRAY + cs.getName()
								+ ChatColor.BLUE + " is teleporting to you.");
						player.teleport(victim.getLocation());
						return true;
					} else {
						cs.sendMessage(ChatColor.RED
								+ "This command cannot be used in console.");
						return true;
					}
				} else {
					cs.sendMessage(ChatColor.RED
							+ "That player does not exist!");
					return true;
				}
			}
		}
		return false;
	}
}
