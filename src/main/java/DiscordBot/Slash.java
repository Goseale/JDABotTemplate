package DiscordBot;

import DiscordBot.Command.ICommandSlash;
import DiscordBot.Util.Commons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.awt.*;
import java.util.LinkedList;

public class Slash {
    public static void updateCommands(JDA jda, CommandManager manager){

        CommandListUpdateAction cmdUpdate = jda.updateCommands();

        LinkedList<String> assigned = new LinkedList<>();
        for (ICommandSlash command : manager.getSlashCommands()) {
            if (command.isHidden()) {
                continue;
            }

            CommandData cmd = Commands.slash(command.getName(), Commons.trimToInt(command.getHelp(), 100));
            if (command.getSlash() != null) {
                cmd = command.getSlash();
            }

            cmdUpdate.addCommands(cmd);
        }
        cmdUpdate.queue();
    }
    public static void updateCommands(JDA jda, CommandManager manager, InteractionHook slash){

        CommandListUpdateAction cmdUpdate = jda.updateCommands();

        LinkedList<String> assigned = new LinkedList<>();
        for (ICommandSlash command : manager.getSlashCommands()) {
            if (command.isHidden()) {
                continue;
            }

            CommandData cmd = Commands.slash(command.getName(), Commons.trimToInt(command.getHelp(),100));
            if (command.getSlash() != null) {
                cmd = command.getSlash();
            }

            cmdUpdate.addCommands(cmd);
        }
        cmdUpdate.queue(
                (__) -> {
                    slash.editOriginal("Slash commands have been updated.").queue();
                },
                (___) -> {
                    slash.editOriginal("**An exception was thrown**").queue();
                    slash.editOriginalEmbeds(
                            new EmbedBuilder()
                                    .setTitle("Exception info")
                                    .setDescription(___.getMessage())
                                    .setColor(Color.RED)
                                    .build()
                    ).queue();
                }
        );
    }
}
