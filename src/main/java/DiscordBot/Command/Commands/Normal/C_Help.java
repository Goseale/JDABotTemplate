package DiscordBot.Command.Commands.Normal;

import DiscordBot.Command.CommandContext;
import DiscordBot.Command.ICommand;
import DiscordBot.CommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class C_Help implements ICommand {
    private final CommandManager manager;

    public C_Help(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();
        EmbedBuilder emb = new EmbedBuilder();

        if (args.isEmpty()) {
            final int[] commandCount = {0};
            List<String> InEmbed = new ArrayList<>();
            CommandManager.addCooldown(ctx,getName());
            emb.setTitle("Available commands");
            emb.setColor(Color.green);
            manager.getCommands().stream().map(ICommand::getCategory).forEach(
                    (it) -> {
                        if (it.equalsIgnoreCase("Owner")) {
                            return;
                        }
                        StringBuilder builder = new StringBuilder();
                        if (InEmbed.contains(it)) return;
                        InEmbed.add(it);
                        for (ICommand command : manager.getCommands()) {
                            if (command.getCategory().equals(it)) {

                                builder.append("`").append(CommandManager.getPrefix).append(command.getName()).append("`\n");
                                commandCount[0] = commandCount[0] +1;

                            }
                        }
                        emb.addField(it,builder.toString(),true);
                    }
            );
            emb.setFooter("Total bot commands: "+ commandCount[0]);
            channel.sendMessage(emb.build()).queue();
            return;
        }
        String search = args.get(0);
        ICommand command = manager.getCommand(search);

        if (command == null || (command.getCategory().equalsIgnoreCase("Owner")) ) {
            emb.setColor(Color.red);
            emb.setTitle("Error!");
            emb.setDescription("No command was found for the query " + search);
            channel.sendMessage(emb.build()).queue();
            return;
        }
        CommandManager.addCooldown(ctx,getName());
        emb.setColor(Color.yellow);
        emb.setTitle("Command help");
        emb.setDescription("**Remember: [ ] ( ) indicates a parameter that should be used in that spot. Do NOT include them in the command or else it wont work**");
        emb.addField("Command", command.getName().toUpperCase(), true);
        emb.addField("Description", command.getHelp(), true);

        emb.addField("Cooldown", command.getCooldown() + " ms", true);
        emb.addField("Aliases", command.getAliases().toString(), true);
        channel.sendMessage(emb.build()).queue();

    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Shows a list with the commands of the bot\n" +
                "Usage " + CommandManager.getPrefix + "help [Command]";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("commands", "cmds", "commandlist");
    }
}
//Comment to rewind changes