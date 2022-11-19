package DiscordBot.Events;

import DiscordBot.Command.ICommandSlash;
import DiscordBot.CommandManager;
import DiscordBot.Util.Commons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SlashCommand extends ListenerAdapter {
    private final CommandManager manager;

    public SlashCommand(CommandManager manager) {
        this.manager = manager;
    }
    public static List<String> cooldown = new ArrayList<>();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String cmd = event.getCommandPath();
        String subcmd = event.getSubcommandName();

        if (subcmd == null) {
            for (ICommandSlash slashCommand : manager.getSlashCommands()) {
                if (slashCommand.getName().equals(cmd)) {
                    slashCommand.handle(event);
                    return;
                }
            }
        }

        for (ICommandSlash slashCommand : manager.getSlashCommands()) {
            if (slashCommand.getName().equals(subcmd)) {
                slashCommand.handle(event);
                return;
            }
        }
        event.reply(":x: Unknown command").setEphemeral(true).queue();
    }


    public static void addCooldown(String authorID, String command) {
        String searchLower = command.toLowerCase();

        ICommandSlash cmdF = null;

        for (ICommandSlash cmd : CommandManager.slashCommands) {
            if (cmd.getName().equals(searchLower)) {
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

        cooldown.add(authorID + command);
        setTimeout(() -> cooldown.remove(authorID + command), cmdF.getCooldown());
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

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
