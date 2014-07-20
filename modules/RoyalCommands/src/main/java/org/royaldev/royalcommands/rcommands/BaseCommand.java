package org.royaldev.royalcommands.rcommands;

import mkremins.fanciful.FancyMessage;
import net.minecraft.util.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.royaldev.royalcommands.AuthorizationHandler;
import org.royaldev.royalcommands.Config;
import org.royaldev.royalcommands.MessageColor;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class BaseCommand implements CommandExecutor {
    final RoyalCommands plugin;
    final AuthorizationHandler ah;
    private final String name;
    private final boolean checkPermissions;

    public BaseCommand(final RoyalCommands instance, final String name, final boolean checkPermissions) {
        this.plugin = instance;
        this.ah = this.plugin.ah;
        this.name = name;
        this.checkPermissions = checkPermissions;
    }

    abstract boolean runCommand(CommandSender cs, Command cmd, String label, String[] args);

    @Override
    public final boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase(this.name)) return false;
        if (this.checkPermissions && !this.ah.isAuthorized(cs, cmd)) {
            RUtils.dispNoPerms(cs, new String[]{this.ah.getPermission(cmd)}); // ensure calling to varargs method
            return true;
        }
        try {
            return this.runCommand(cs, cmd, label, args);
        } catch (Throwable t) {
            this.handleException(cs, cmd, label, args, t);
            return true;
        }
    }

    CommandArguments getCommandArguments(String[] args) {
        return new CommandArguments(args);
    }

    private String hastebin(String paste) throws IOException {
        final URL obj = new URL("http://hastebin.com/documents");
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        final DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(paste);
        wr.flush();
        wr.close();
        final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        final StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) response.append(inputLine);
        in.close();
        final HastebinData hd = new Gson().fromJson(response.toString(), HastebinData.class);
        return "http://hastebin.com/" + hd.getKey() + ".txt";
    }

    private void scheduleHastebin(final CommandSender cs, final String paste) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                String tempURL;
                try {
                    tempURL = BaseCommand.this.hastebin(paste);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    tempURL = null;
                }
                final String url = tempURL;
                BaseCommand.this.plugin.getServer().getScheduler().runTask(BaseCommand.this.plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (url != null) {
                            BaseCommand.this.plugin.getLogger().warning("Error paste: " + url);
                            // @formatter:off
                            new FancyMessage("Click ")
                                    .color(MessageColor.NEGATIVE._())
                                .then("here")
                                    .color(MessageColor.NEUTRAL._())
                                    .tooltip("Click here to find out more.")
                                    .link(url)
                                .then(" to find out more.")
                                    .color(MessageColor.NEGATIVE._())
                                .send(cs);
                            // @formatter:on
                        } else {
                            new FancyMessage(Config.hastebinErrors ? "An error occurred while trying to paste the stack trace." : "Error pasting is disabled.").color(MessageColor.NEGATIVE._()).send(cs);
                        }
                    }
                });
            }
        });
    }

    private void handleException(CommandSender cs, Command cmd, String label, String[] args, Throwable t) {
        new FancyMessage("An exception occurred while processing that command.").color(MessageColor.NEGATIVE._()).send(cs);
        t.printStackTrace();
        if (Config.hastebinErrors) {
            final StringBuilder sb = new StringBuilder();
            sb
                    // @formatter:off
                    .append("An error occurred while handling a command. Please report this to jkcclemens or WizardCM.\n")
                    .append("They are available at #bukkit @ irc.royaldev.org. If you don't know what that means, then\n")
                    .append("go to the following URL: https://irc.royaldev.org/#bukkit\n\n")
                    .append("---DEBUG INFO---\n\n");
                    // @formatter:on
            if (cs != null) {
                sb
                        // @formatter:off
                        .append("CommandSender\n")
                        .append("\tName:\t\t").append(cs.getName()).append("\n")
                        .append("\tClass:\t\t").append(cs.getClass().getName());
                        // @formatter:on
            } else sb.append("CommandSender:\t\tnull");
            if (cmd != null) {
                sb
                        // @formatter:off
                        .append("\n\nCommand\n")
                        .append("\tName:\t\t").append(cmd.getName()).append("\n")
                        .append("\tClass:\t\t").append(cmd.getClass().getName());
                        // @formatter:on
            } else sb.append("\n\nCommand:\t\tnull");
            sb.append("\n\nLabel:\t\t").append(label);
            sb.append("\n\nArguments");
            if (args != null) {
                for (final String arg : args) sb.append("\n").append("\t").append(arg);
            } else sb.append("\t\tnull");
            final StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            sb.append("\n\n---STRACK TRACE---\n\n").append(sw.toString());
            this.scheduleHastebin(cs, sb.toString());
        }
    }

    private class HastebinData {
        @SuppressWarnings("UnusedDeclaration")
        private String key;

        public String getKey() {
            return this.key;
        }
    }

    class CommandArguments extends HashMap<String, String[]> {

        private String[] extraParameters = new String[0];

        CommandArguments(final String[] givenArguments) {
            this.processArguments(givenArguments);
        }

        CommandArguments(final String givenArguments) {
            this(givenArguments.split(" "));
        }

        private boolean isFlag(String s) {
            return s.startsWith("-") && !this.isFlagTerminator(s);
        }

        private boolean isFlagTerminator(String s) {
            return s.equals("--");
        }

        private String getFlagName(String s) {
            if (!this.isFlag(s)) throw new IllegalArgumentException("Not a flag.");
            return s.substring(s.length() > 2 && s.substring(1).startsWith("-") ? 2 : 1);
        }

        String[] getExtraParameters() {
            return this.extraParameters.clone();
        }

        String[] getFlag(String... flags) {
            final List<String> combinedParameters = new ArrayList<>();
            for (String flag : flags) {
                if (!this.containsKey(flag)) continue;
                combinedParameters.addAll(Arrays.asList(this.get(flag)));
            }
            return combinedParameters.toArray(new String[combinedParameters.size()]);
        }

        String getFlagString(String... flags) {
            return RUtils.join(this.getFlag(flags), " ");
        }

        /**
         * Checks if flags are set. Useful for boolean flags.
         *
         * @param flags Flags to check for
         * @return boolean
         */
        boolean hasFlag(String... flags) {
            for (final String flag : flags) {
                if (!this.containsKey(flag)) continue;
                return true;
            }
            return false;
        }

        void processArguments(String[] arguments) {
            String currentFlag = null;
            final List<String> parameters = new ArrayList<>();
            final List<String> extraParameters = new ArrayList<>();
            for (String arg : arguments) {
                if (this.isFlag(arg) || this.isFlagTerminator(arg)) {
                    this.put(currentFlag, parameters.toArray(new String[parameters.size()]));
                    parameters.clear();
                    currentFlag = this.isFlagTerminator(arg) ? null : this.getFlagName(arg);
                    continue;
                }
                arg = arg.replace("\\-", "-");
                if (currentFlag != null) parameters.add(arg);
                else extraParameters.add(arg);
            }
            this.put(currentFlag, parameters.toArray(new String[parameters.size()])); // last arg can't be neglected
            this.extraParameters = ArrayUtils.addAll(this.extraParameters, extraParameters.toArray(new String[extraParameters.size()]));
        }
    }
}
