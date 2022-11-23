package DiscordBot.Command.Slash.Owner;

import DiscordBot.Command.ICommandSlash;
import DiscordBot.CommandManager;
import DiscordBot.Util.Commons;
import DiscordBot.Util.Values;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import groovy.lang.GroovyShell;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class SC_Eval implements ICommandSlash {
    private final GroovyShell engine;
    private final String imports;
    private final EventWaiter waiter;
    private final CommandManager manager;

    public SC_Eval(EventWaiter waiter, CommandManager manager) {
        this.waiter = waiter;
        this.engine = new GroovyShell();
        this.imports = "import java.io.*\n" +
                "import java.lang.*\n" +
                "import java.util.*\n" +
                "import java.util.concurrent.*\n" +
                "import net.dv8tion.jda.api.*;\n" +
                "import net.dv8tion.jda.api.entities.*;\n" +
                "import net.dv8tion.jda.api.managers.*;" +
                "import net.dv8tion.jda.api.events.*;\n" +
                "import net.dv8tion.jda.api.events.interaction.*;\n" +
                "import net.dv8tion.jda.api.interactions.*;\n" +
                "import net.dv8tion.jda.api.interactions.commands.*;\n" +
                "import net.dv8tion.jda.api.interactions.commands.build.*;\n" +
                "import DiscordBot.*;\n" +
                "";
        this.manager = manager;
    }

    @Override
    public void handle(SlashCommandInteractionEvent ctx) {
        Member member = ctx.getMember();
        EmbedBuilder emb = new EmbedBuilder();
        if (!ctx.getUser().getId().equals(Values.OWNER_ID)) {
            emb.setColor(Color.red);
            emb.setTitle("Oops");
            emb.setDescription("It looks like you are lacking the ownership of this bot\n" +
                    "When you get that, try this command again");
            ctx.replyEmbeds(emb.build()).queue();
            return;
        }

        Modal.Builder modal = Modal.create(ctx.getUser().getId()+":Eval", "Eval");
        TextInput TIcode = TextInput.create("code", "What to execute in the bot", TextInputStyle.PARAGRAPH).build();
        modal.addActionRow(TIcode);

        ctx.replyModal(modal.build()).queue(
                __ -> {
                    awaitModal(ctx, emb, waiter, manager);
                }
        );
    }

    private void awaitModal(SlashCommandInteractionEvent ctx, EmbedBuilder emb, EventWaiter waiter, CommandManager manager) {
        waiter.waitForEvent(
                ModalInteractionEvent.class, (event) -> {
                    User user = event.getUser();
                    return user.getId().equals("229016449593769984") && event.getInteraction().getModalId().contains(ctx.getUser().getId()+":Eval");
                },
                (event) -> {

                    String code = event.getValue("code").getAsString();

                    if (code.toLowerCase().contains("token")) {
                        EmbedBuilder error = Commons.coolEmbedGet(ctx, "Error", "Nah pal that ain't happening", Color.red);
                        event.replyEmbeds(error.build()).setEphemeral(true).queue();
                        return;
                    }

                    Commons.coolEmbedGet(ctx.getJDA(), emb, "Eval recieved", "Evalling code...", Color.YELLOW);
                    event.replyEmbeds(emb.build()).queue(
                            interactionHook -> {
                                eval(code, ctx, emb, interactionHook, waiter, manager);
                            }
                    );

                },
                30, TimeUnit.SECONDS,
                () -> {

                }
        );
    }

    @Override
    public String getName() {
        return "eval";
    }

    @Override
    public String getHelp() {
        return "Owner Only | Runs java code in the bot";
    }

    @Override
    public CommandDataImpl getSlash() {
        CommandDataImpl cmd = new CommandDataImpl(this.getName(), this.getHelp());
        return cmd;
    }

    public void eval(String args, SlashCommandInteractionEvent event, EmbedBuilder emb, InteractionHook message, EventWaiter waiter, CommandManager manager) {
        try {
            engine.setProperty("args", args);
            engine.setProperty("event", event);
            engine.setProperty("channel", event.getChannel());
            engine.setProperty("jda", event.getJDA());
            engine.setProperty("guild", event.getGuild());
            engine.setProperty("member", event.getMember());
            engine.setProperty("ctx", event);
            engine.setProperty("waiter", waiter);
            engine.setProperty("manager", manager);
            engine.setProperty("message", message);

            String script = imports + args;
            Object out = engine.evaluate(script);

            emb.setColor(Color.BLUE);
            emb.setTitle("Eval results");
            if (out == null) {
                emb = Commons.coolEmbedGet(event, "Eval", "Returned no errors", Color.BLUE);
            } else {
                emb = Commons.coolEmbedGet(event, "Eval", "", Color.BLUE);
                emb.setDescription("```" + out.toString() + "```");
            }
            EmbedBuilder finalEmb = emb;
            message.editOriginalEmbeds(emb.build()).queue(
                    message1 -> {},
                    (__) -> {
                        finalEmb.setDescription("```" + "Output too large to display" + "```");
                        message.editOriginalEmbeds(finalEmb.build()).queue(
                                message1 -> {}
                        );
                    }
            );
        } catch (Exception e) {
            emb.setColor(Color.orange);
            emb.setTitle("Eval results");
            emb.setDescription("```" + e.getMessage() + "```");
            EmbedBuilder finalEmb1 = emb;
            message.editOriginalEmbeds(emb.build()).queue();
        }
    }

}
