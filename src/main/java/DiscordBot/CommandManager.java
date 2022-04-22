package DiscordBot;

import DiscordBot.Command.CommandContext;
import DiscordBot.Command.Commands.Normal.C_Eval;
import DiscordBot.Command.Commands.Normal.C_Help;
import DiscordBot.Command.Commands.Normal.C_Ping;
import DiscordBot.Command.ICommand;
import DiscordBot.Command.ICommandSlash;
import DiscordBot.Command.Slash.Normal.SC_Help;
import DiscordBot.Command.Slash.Normal.SC_Ping;
import DiscordBot.Command.Slash.Owner.SC_UpdateSlashes;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CommandManager {
    public final static List<ICommand> commands = new ArrayList<>();
    public final static List<ICommandSlash> slashCommands = new ArrayList<ICommandSlash>();

    public static List<String> cooldown = new ArrayList<>();
    public static List<String> blockedCMD = new ArrayList<>();

    public static String getPrefix = null;

    public CommandManager(EventWaiter waiter, JDA jda) {

        addCommand(new C_Ping());
        addCommand(new C_Help(this));
        addCommand(new C_Eval(waiter,this));

        addSlashCommand(new SC_UpdateSlashes(jda, this));
        addSlashCommand(new SC_Help(this,waiter));
        addSlashCommand(new SC_Ping());

        //Uncomment the line below on the first run to register the slash commands
        //Slash.updateCommands(jda,this);

    }

    private void addCommand(ICommand cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound) {
            System.out.println("===[ COMMAND WITH SAME NAME FOUND! ]===");
            System.out.println(cmd.getName());
            System.out.println("=======================================");
            throw new IllegalArgumentException("A command with this name is already present");
        }

        commands.add(cmd);
    }

    private void addSlashCommand(ICommandSlash cmd) {
        boolean nameFound = this.slashCommands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound) {
            System.out.println("===[ COMMAND WITH SAME NAME FOUND! ]===");
            System.out.println(cmd.getName());
            System.out.println("=======================================");
            throw new IllegalArgumentException("A command with this name is already present");
        }

        slashCommands.add(cmd);
    }

    public List<ICommand> getCommands() {
        return commands;
    }
    public List<ICommandSlash> getSlashCommands() {return slashCommands;}

    @Nullable
    public ICommand getCommand(String search) {
        String searchLower = search.toLowerCase();

        for (ICommand cmd : this.commands) {
            if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
                return cmd;
            }
        }

        return null;
    }

    void handle(GuildMessageReceivedEvent event, String prefix) {

        String raw = event.getMessage().getContentRaw();
        String[] split = raw
                .replaceFirst("(?i)" + Pattern.quote(prefix), "")
                .split("\\s+");

        String invoke = split[0].toLowerCase();
        ICommand cmd = this.getCommand(invoke);
        if (cmd != null) {

            if (cooldown.contains(event.getAuthor().getId() + invoke)) {
                if (event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                    event.getMessage().delete().queue();
                }
                EmbedBuilder emb = new EmbedBuilder();
                emb.setColor(Color.red);
                emb.setTitle("`" + capitalize(invoke.toLowerCase()) + "` is still on cooldown!");
                event.getChannel().sendMessage(emb.build()).delay(Duration.ofSeconds(3)).queue(
                        (message -> {
                            message.delete().queue();
                        })
                );
                return;
            }
            if (cooldown.contains(event.getAuthor().getId() + invoke)) {
                final TextChannel channel = event.getChannel();
                EmbedBuilder emb = new EmbedBuilder();
                emb.setTitle(event.getAuthor().getName()+" - Cooldown bypassed");
                channel.sendMessage(emb.build()).queue(
                        message -> {
                            message.delete().queueAfter(3, TimeUnit.SECONDS);
                        }
                );
            }



            if (cmd.getName().equalsIgnoreCase("ping")) {

                List<String> args = Arrays.asList(split).subList(1, split.length);

                CommandContext ctx = new CommandContext(event, args);

                if (blockedCMD.contains(cmd.getName())) {
                    EmbedBuilder emb = new EmbedBuilder();
                    emb.setColor(Color.RED);
                    emb.setTitle("Command blocked!");
                    emb.setDescription("The owner has temporarily blocked this command from being used");
                    ctx.getChannel().sendMessage(emb.build()).queue();
                    return;
                }

                getPrefix = prefix;
                cmd.handle(ctx);

                return;
            }
            event.getChannel().sendTyping().queue(
                    (__) -> {
                        List<String> args = Arrays.asList(split).subList(1, split.length);

                        CommandContext ctx = new CommandContext(event, args);

                        getPrefix = prefix;
                        cmd.handle(ctx);
                    }
            );

        } else {

        }
    }

    public static void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                System.err.println(e);
            }
        }).start();
    }

    public static void addCooldown(CommandContext ctx, int timeInMS, String command) {
        cooldown.add(ctx.getAuthor().getId() + command);
        setTimeout(() -> cooldown.remove(ctx.getAuthor().getId() + command), timeInMS);
    }

    public static void addCooldown(CommandContext ctx, String command) {
        String searchLower = command.toLowerCase();

        ICommand cmdF = null;

        for (ICommand cmd : commands) {
            if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
                cmdF = cmd;
            }
        }

        try {
            if (cmdF.getCooldown() == 0) {
                return;
            }
        } catch (Exception e) {
            return;
        }

        cooldown.add(ctx.getAuthor().getId() + command);
        setTimeout(() -> cooldown.remove(ctx.getAuthor().getId() + command), cmdF.getCooldown());
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}