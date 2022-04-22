package DiscordBot;

import DiscordBot.Command.ICommandSlash;
import DiscordBot.Util.Commons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.awt.*;
import java.util.LinkedList;

public class Slash {
    public static void updateCommands(JDA jda, CommandManager manager){

        CommandListUpdateAction cmdUpdate = jda.updateCommands();

        LinkedList<String> assigned = new LinkedList<>();
        manager.getSlashCommands().stream().map(ICommandSlash::getTopCommand).forEach(
                (it) -> {
                    if (assigned.contains(it)) return;
                    assigned.add(it);

                    if (it.equals("_")) {

                        for (ICommandSlash command : manager.getSlashCommands()) {
                            if (command.getTopCommand().equals(it)) {
                                if (command.isHidden()) {
                                    continue;
                                }

                                CommandData cmd = new CommandData(command.getName().replace("g.", "gose").replace("{.","gose"), Commons.trimToInt("|"+command.getHelp(), 100));
                                if (command.getSlash() != null) {
                                    cmd = command.getSlash();
                                }

                                cmdUpdate.addCommands(cmd);
                            }
                        }

                        return;
                    }





                    CommandData category = new CommandData(it.toLowerCase(), "Command category");


                    for (ICommandSlash command : manager.getSlashCommands()) {
                        if (command.getTopCommand().equals(it)) {
                            if (command.isHidden()) {
                                continue;
                            }

                            SubcommandData cmd = new SubcommandData(command.getName().replace("g.", "gose").replace("{.","gose"), Commons.trimToInt("|"+command.getHelp(), 100));
                            if (command.getSubSlash() != null) {
                                cmd = command.getSubSlash();
                            }

                            category.addSubcommands(cmd);
                        }
                    }

                    cmdUpdate.addCommands(category);
                }
        );
        cmdUpdate.queue();
    }
    public static void updateCommands(JDA jda, CommandManager manager, InteractionHook slash){

        CommandListUpdateAction cmdUpdate = jda.updateCommands();

        LinkedList<String> assigned = new LinkedList<>();
        manager.getSlashCommands().stream().map(ICommandSlash::getTopCommand).forEach(
                (it) -> {
                    if (assigned.contains(it)) return;
                    assigned.add(it);

                    if (it.equals("_")) {

                        for (ICommandSlash command : manager.getSlashCommands()) {
                            if (command.getTopCommand().equals(it)) {
                                if (command.isHidden()) {
                                    continue;
                                }

                                CommandData cmd = new CommandData(command.getName().replace("g.", "gose").replace("{.","gose"), Commons.trimToInt("|"+command.getHelp(), 100));
                                if (command.getSlash() != null) {
                                    cmd = command.getSlash();
                                }

                                cmdUpdate.addCommands(cmd);
                            }
                        }

                        return;
                    }





                    CommandData category = new CommandData(it.toLowerCase(), "Command category");


                    for (ICommandSlash command : manager.getSlashCommands()) {
                        if (command.getTopCommand().equals(it)) {
                            if (command.isHidden()) {
                                continue;
                            }

                            SubcommandData cmd = new SubcommandData(command.getName().replace("g.", "gose").replace("{.","gose"), Commons.trimToInt("|"+command.getHelp(), 100));
                            if (command.getSubSlash() != null) {
                                cmd = command.getSubSlash();
                            }

                            category.addSubcommands(cmd);
                        }
                    }

                    cmdUpdate.addCommands(category);
                }
        );
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
