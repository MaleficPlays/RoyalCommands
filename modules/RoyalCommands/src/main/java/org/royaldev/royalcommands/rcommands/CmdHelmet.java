package org.royaldev.royalcommands.rcommands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.royaldev.royalcommands.Config;
import org.royaldev.royalcommands.MessageColor;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;
import org.royaldev.royalcommands.configuration.PConfManager;
import org.royaldev.royalcommands.exceptions.InvalidItemNameException;

@ReflectCommand
public class CmdHelmet extends BaseCommand {

    public CmdHelmet(final RoyalCommands instance, final String name) {
        super(instance, name, true);
    }

    @Override
    public boolean runCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(MessageColor.NEGATIVE + "This command is only available to players!");
            return true;
        }
        if (args.length < 1) {
            cs.sendMessage(cmd.getDescription());
            return false;
        }
        Player p = (Player) cs;
        PConfManager pcm = PConfManager.getPConfManager(p);
        String name = args[0];
        if (name.equalsIgnoreCase("none")) {
            ItemStack helm = p.getInventory().getHelmet();
            if (Config.requireHelm) {
                if (pcm.getString("helmet") != null) {
                    ItemStack stack = RUtils.getItem(pcm.getString("helmet"), 1);
                    if (stack == null) {
                        cs.sendMessage(MessageColor.NEGATIVE + "You have no helmet!");
                        return true;
                    }
                    if (helm == null || helm.getType() == Material.AIR || helm.getType() != stack.getType()) {
                        p.sendMessage(MessageColor.NEGATIVE + "You have already removed your helmet!");
                        pcm.set("helmet", null);
                        return true;
                    }
                    p.getInventory().addItem(stack);
                }
            }
            p.getInventory().setHelmet(null);
            p.sendMessage(MessageColor.POSITIVE + "Removed your helmet.");
            return true;
        }
        ItemStack stack = RUtils.getItem(name, 1);
        if (stack == null) {
            try {
                stack = RUtils.getItemFromAlias(name, 1);
            } catch (InvalidItemNameException e) {
                cs.sendMessage(MessageColor.NEGATIVE + "Invalid item name!");
                return true;
            } catch (NullPointerException e) {
                cs.sendMessage(MessageColor.NEGATIVE + "ItemNameManager was not loaded. Let an administrator know.");
                return true;
            }
        }
        if (stack == null) {
            p.sendMessage(MessageColor.NEGATIVE + "Invalid item name!");
            return true;
        }
        if (Config.requireHelm) {
            if (!p.getInventory().contains(stack)) {
                p.sendMessage(MessageColor.NEGATIVE + "You don't have that item!");
                return true;
            }
            pcm.set("helmet", stack.getType().name());
            p.getInventory().remove(stack);
        }
        p.getInventory().setHelmet(stack);
        p.sendMessage(MessageColor.POSITIVE + "Set your helmet to " + MessageColor.NEUTRAL + RUtils.getFriendlyEnumName(stack.getType()) + MessageColor.POSITIVE + ".");
        return true;
    }
}
