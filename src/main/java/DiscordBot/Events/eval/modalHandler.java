package DiscordBot.Events.eval;

import DiscordBot.CommandManager;
import DiscordBot.Util.Commons;
import groovy.lang.GroovyShell;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

import javax.annotation.Nonnull;
import java.awt.*;

public class modalHandler extends ListenerAdapter {

    private final SlashCommandInteractionEvent ctx;
    private final EmbedBuilder emb;
    private final CommandManager manager;
    private final GroovyShell engine;
    private final String imports;

    public modalHandler(SlashCommandInteractionEvent ctx, EmbedBuilder emb, CommandManager manager) {
        this.ctx = ctx;
        this.emb = emb;
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
        ctx.getJDA().addEventListener(this);
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        User user = event.getUser();
        boolean activate = user.getId().equals("229016449593769984") && event.getInteraction().getModalId().contains(ctx.getUser().getId()+":Eval");
        if (activate) {
            ctx.getJDA().removeEventListener(this);
            String code = event.getValue("code").getAsString();

            if (code.toLowerCase().contains("token")) {
                EmbedBuilder error = Commons.coolEmbedGet(ctx, "Error", "Nah pal that ain't happening", Color.red);
                event.replyEmbeds(error.build()).setEphemeral(true).queue();
                return;
            }

            Commons.coolEmbedGet(ctx.getJDA(), emb, "Eval recieved", "Evalling code...", Color.YELLOW);
            event.replyEmbeds(emb.build()).queue(
                    interactionHook -> {
                        eval(code, ctx, emb, interactionHook, manager);
                    }
            );
        }


    }

    public void eval(String args, SlashCommandInteractionEvent event, EmbedBuilder emb, InteractionHook message, CommandManager manager) {
        try {
            engine.setProperty("args", args);
            engine.setProperty("event", event);
            engine.setProperty("channel", event.getChannel());
            engine.setProperty("jda", event.getJDA());
            engine.setProperty("guild", event.getGuild());
            engine.setProperty("member", event.getMember());
            engine.setProperty("ctx", event);
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
