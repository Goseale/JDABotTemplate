package DiscordBot.Events.help;

import DiscordBot.Command.ICommandSlash;
import DiscordBot.CommandManager;
import DiscordBot.Util.Values;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;
import java.util.LinkedList;

public class selectionMenu extends ListenerAdapter {

    private final SlashCommandInteractionEvent ctx;
    private final InteractionHook message;
    private final LinkedList<String> rIndicators;
    private final LinkedList<String> assigned;
    private final EmbedBuilder emb;
    private final CommandManager manager;
    private final String description;

    public selectionMenu(SlashCommandInteractionEvent ctx, CommandManager manager, String description, InteractionHook message, LinkedList<String> rIndicators, LinkedList<String> assigned, EmbedBuilder emb) {
        this.ctx = ctx;
        this.message = message;
        this.rIndicators = rIndicators;
        this.assigned = assigned;
        this.emb = emb;
        this.manager = manager;
        this.description = description;
        ctx.getJDA().addEventListener(this);
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        User user = event.getUser();

        boolean activate = event.getInteraction().getSelectedOptions().get(0).getValue().contains("help"+event.getUser().getId()+"_") && !user.isBot() && event.getChannel().getId().equals(ctx.getChannel().getId());
        if (activate) {
            event.getJDA().removeEventListener(this);
            MessageCreateBuilder msb = new MessageCreateBuilder();
            String id = event.getInteraction().getSelectedOptions().get(0).getValue();

            try {
                if (Integer.parseInt(id.split("_")[2]) >= 0) {
                    int rint = Integer.parseInt(id.split("_")[2]);
                    if (rint <= (assigned.size() - 1)) {
                        String cat = assigned.get(rint);

                        emb.clear();
                        emb.setColor(Color.green);
                        emb.setTitle("Help menu");
                        emb.setDescription(description);
                        emb.addField("Category name", cat, true);

                        StringBuilder sb = new StringBuilder();
                        int count = 1;

                        for (ICommandSlash command : manager.getSlashCommands()) {
                            if (command.getCategory().equals(cat)) {

                                if (command.isHidden() && (!ctx.getGuild().getId().equals("348649372449243137"))) {
                                    continue;
                                }

                                if (Values.COMMAND_LIST != null && sb.length() < 768) {
                                    Command dSlashCMD = null;
                                    for (Command slashCMD : Values.COMMAND_LIST) {
                                        if (slashCMD.getName().equals(command.getName())) {
                                            dSlashCMD = slashCMD;
                                            break;
                                        }
                                    }
                                    if (dSlashCMD != null) {
                                        if (count % 5 == 0) {
                                            sb.append("</" + dSlashCMD.getName() + ":"+dSlashCMD.getId()+">\n");
                                        } else {
                                            sb.append("</" + command.getName() + ":"+dSlashCMD.getId()+"> | ");
                                        }
                                    } else {
                                        if (count % 5 == 0) {
                                            sb.append("`" + command.getName() + "`\n");
                                        } else {
                                            sb.append("`" + command.getName() + "` | ");
                                        }
                                    }
                                } else {
                                    if (count % 5 == 0) {
                                        sb.append("`" + command.getName() + "`\n");
                                    } else {
                                        sb.append("`" + command.getName() + "` | ");
                                    }
                                }



                                count++;

                            }
                        }

                        emb.addField("Commands", sb.toString(), true);
                        event.reply(msb.setEmbeds(emb.build()).build()).setEphemeral(true).queue(
                                message1 -> {
                                    new selectionMenu(ctx, manager, description, message, rIndicators, assigned, emb);
                                }, (___) -> {}
                        );


                    } else {
                        new selectionMenu(ctx, manager, id, message, rIndicators, assigned, emb);
                    }
                } else {
                    if (Integer.parseInt(id.split("_")[2]) == -1) {
                        emb.clear();
                        emb.setColor(Color.green);
                        emb.setTitle("Help menu");

                        StringBuilder sb = new StringBuilder();
                        int count = 1;
                        for (ICommandSlash command : manager.getSlashCommands()) {
                            if (!command.getCategory().equals("Owner")) {

                                if (count % 5 == 0) {
                                    sb.append("`" + command.getName() + "`\n");
                                } else {
                                    sb.append("`" + command.getName() + "` | ");
                                }

                                count++;

                            }

                        }
                        emb.setDescription(description + "\n\n" + "" +
                                "**All commands:** " + sb.toString());
                        event.reply(msb.setEmbeds(emb.build()).build()).setEphemeral(true).queue(
                                message1 -> {
                                    new selectionMenu(ctx, manager, id, message, rIndicators, assigned, emb);
                                }
                        );


                    }
                    new selectionMenu(ctx, manager, id, message, rIndicators, assigned, emb);
                }

            } catch (Exception e) {
                new selectionMenu(ctx, manager, id, message, rIndicators, assigned, emb);
            }
        }

    }
}
