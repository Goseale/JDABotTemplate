package DiscordBot.Command.Slash.Normal;

import DiscordBot.Command.ICommandSlash;
import DiscordBot.CommandManager;
import DiscordBot.Util.Values;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SC_Help implements ICommandSlash {
    private final EventWaiter waiter;
    private final CommandManager manager;
    private String description = "";

    public SC_Help(CommandManager manager, EventWaiter waiter) {
        this.manager = manager;
        this.waiter = waiter;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        EmbedBuilder emb = new EmbedBuilder();

        LinkedList<String> rIndicators = new LinkedList<>(Arrays.asList(new String[]{"\uD83D\uDD10", "\uD83E\uDDF6", "\uD83D\uDEE0", "\uD83D\uDC7E", "\uD83D\uDD2C", "\uD83E\uDD16", "\uD83D\uDCFB", "\uD83C\uDDED", "\uD83C\uDDEE", "\uD83C\uDDEF", "\uD83C\uDDF0", "\uD83C\uDDF1", "\uD83C\uDDF2", "\uD83C\uDDF3", "\uD83C\uDDF4", "\uD83C\uDDF5", "\uD83C\uDDF6", "\uD83C\uDDF7", "\uD83C\uDDF8", "\uD83C\uDDF9", "\uD83C\uDDFA", "\uD83C\uDDFB", "\uD83C\uDDFC", "\uD83C\uDDFD", "\uD83C\uDDFE", "\uD83C\uDDFF"}));
        LinkedList<String> assigned = new LinkedList<>();
        LinkedList<Integer> count = new LinkedList<>();

        if (event.getOption("command") == null) {
            final int[] commandCount = {0};
            List<String> InEmbed = new ArrayList<>();
            emb.setTitle("Help menu slash");
            emb.setColor(Color.green);
            manager.getSlashCommands().stream().map(ICommandSlash::getCategory).forEach(
                    (it) -> {
                        if (assigned.contains(it)) return;
                        assigned.add(it);
                        count.add(0);
                        commandCount[0] = 0;
                        for (ICommandSlash command : manager.getSlashCommands()) {
                            commandCount[0]++;
                            if (command.getCategory().equals(it)) {

                                if (command.isHidden() && (!event.getGuild().getId().equals("348649372449243137"))) {
                                    continue;
                                }

                                //Count how many cmds there are
                                count.set(assigned.indexOf(it), count.get(assigned.indexOf(it)) + 1);
                            }
                        }
                    }

            );

            StringBuilder sb = new StringBuilder();
            for (String rIndicator : rIndicators) {
                if (rIndicators.indexOf(rIndicator) <= (assigned.size() - 1)) {
                    String catName = assigned.get(rIndicators.indexOf(rIndicator));
                    int cmds = count.get((rIndicators.indexOf(rIndicator)));
                    sb.append(rIndicator + " | " + catName + " - **" + cmds + "** cmds in this category\n");
                }
            }

            emb.setDescription(description + "\n\n" + sb.toString() +
                    "\uD83D\uDCF0 | All commands");
            emb.setFooter("Total bot commands: " + commandCount[0]);
            event.replyEmbeds(emb.build()).setEphemeral(true).queue(
                    message -> {
                        spawnReactions(event, message, rIndicators, assigned, emb);
                    }
            );
            return;
        }
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Description";
    }

    @Override
    public CommandData getSlash() {
        return Commands.slash(this.getName(), "Shows all slash commands of the bot");
    }

    public void spawnReactions(SlashCommandInteractionEvent ctx, InteractionHook message, LinkedList<String> rIndicators, LinkedList<String> assigned, EmbedBuilder emb) {
        SelectMenu.Builder selm = SelectMenu.create("menu:help");
        selm.setPlaceholder("Choose a section");
        selm.setRequiredRange(1,1);

        List<Component> buttons = new ArrayList<>();
        List<ActionRow> actionRows = new ArrayList<>();

        for (String rIndicator : rIndicators) {
            if (rIndicators.indexOf(rIndicator) <= (assigned.size() - 1)) {
                selm.addOption(rIndicator+" "+assigned.get(rIndicators.indexOf(rIndicator)),"help"+ctx.getUser().getId()+"_menu_"+rIndicators.indexOf(rIndicator));
            }
        }
        selm.addOption("\uD83D\uDCF0 All commands", "help"+ctx.getUser().getId()+"_menu_-1");
        message.editOriginalEmbeds(emb.build()).setComponents(ActionRow.of(selm.build())).queue();
        manageReactions(ctx, message, rIndicators, assigned, emb);
    }

    public void manageReactions(SlashCommandInteractionEvent ctx, InteractionHook message, LinkedList<String> rIndicators, LinkedList<String> assigned, EmbedBuilder emb) {
        waiter.waitForEvent(
                SelectMenuInteractionEvent.class, (event) -> {
                    User user = event.getUser();
                    return event.getInteraction().getSelectedOptions().get(0).getValue().contains("help"+event.getUser().getId()+"_") && !user.isBot() && event.getChannel().getId().equals(ctx.getChannel().getId());
                },
                (event) -> {

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
                                            manageReactions(ctx, message, rIndicators, assigned, emb);
                                        }, (___) -> {}
                                );


                            } else {
                                manageReactions(ctx, message, rIndicators, assigned, emb);
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
                                            manageReactions(ctx, message, rIndicators, assigned, emb);
                                        }
                                );


                            }
                            manageReactions(ctx, message, rIndicators, assigned, emb);
                        }

                    } catch (Exception e) {
                        manageReactions(ctx, message, rIndicators, assigned, emb);
                    }

                },
                3, TimeUnit.MINUTES,
                () -> {
                    emb.clear();
                    emb.setColor(Color.yellow);
                    emb.setTitle("The help menu timed out");
                    message.editOriginalEmbeds(emb.build()).setComponents(ActionRow.of(Button.secondary(".","Help timed out").asDisabled())).queue(
                            (__) -> {},
                            (___) -> {}
                    );
                }
        );
    }

}
