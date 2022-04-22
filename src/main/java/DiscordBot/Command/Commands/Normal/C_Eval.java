package DiscordBot.Command.Commands.Normal;

import DiscordBot.Command.CommandContext;
import DiscordBot.Command.ICommand;
import DiscordBot.CommandManager;
import DiscordBot.Configuration;
import DiscordBot.Util.Commons;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import groovy.lang.GroovyShell;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class C_Eval implements ICommand {
    private final GroovyShell engine;
    private final String imports;
    private final EventWaiter waiter;
    private final CommandManager manager;

    public C_Eval(EventWaiter waiter, CommandManager manager) {
        this.waiter = waiter;
        this.engine = new GroovyShell();
        this.imports = "import java.io.*\n" +
                "import java.lang.*\n" +
                "import java.util.*\n" +
                "import java.util.concurrent.*\n" +
                "import net.dv8tion.jda.api.*;\n" +
                "import net.dv8tion.jda.api.entities.*;\n" +
                "import net.dv8tion.jda.api.managers.*;\n" +
                "import GosealeBot.*;\n" +
                "";
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        Member member = ctx.getMember();
        EmbedBuilder emb = new EmbedBuilder();
        if (!member.getUser().getId().equals(Configuration.get("OWNER_ID"))) {
            emb.setColor(Color.red);
            emb.setTitle("Oops");
            emb.setDescription("It looks like you are lacking the ownership of this bot\n" +
                    "When you get that, try this command again");
            channel.sendMessage(emb.build()).queue();
            return;
        }
        List<String> args = ctx.getArgs();
        GuildMessageReceivedEvent event = ctx.getEvent();
        if (args.isEmpty()) {
            emb.setColor(Color.red);
            emb.setTitle("Missing arguments");
            event.getChannel().sendMessage(emb.build()).queue();

            return;
        }

        emb.setTitle("Evaling code...");
        emb.setColor(Color.yellow);


        if (event.getMessage().getContentRaw().toLowerCase().contains("token")) {
            Commons.coolEmbed(ctx,channel,"Error", "Nah pal that ain't happening", Color.red);
            return;
        }


        channel.sendMessage(emb.build()).queue(
                (message) -> {
                    eval(ctx, args, event, emb, message, event.getMessage().getContentRaw(), waiter, manager);
                }
        );

    }

    @Override
    public String getName() {
        return "eval";
    }

    @Override
    public String getHelp() {
        return "Evaluates a given pice of code and executes it\n" +
                "Owner only";
    }

    @Override
    public String getCategory() {
        return "Owner";
    }

    public void eval(CommandContext ctx, List<String> args, GuildMessageReceivedEvent event, EmbedBuilder emb, Message message, String raw, EventWaiter waiter, CommandManager manager) {
        try {
            engine.setProperty("args", args);
            engine.setProperty("event", event);
            engine.setProperty("message", event.getMessage());
            engine.setProperty("channel", event.getChannel());
            engine.setProperty("jda", event.getJDA());
            engine.setProperty("guild", event.getGuild());
            engine.setProperty("member", event.getMember());
            engine.setProperty("ctx", ctx);
            engine.setProperty("waiter", waiter);
            engine.setProperty("manager", manager);

            String script = imports + raw.split("\\s+", 2)[1];
            Object out = engine.evaluate(script);

            emb.setColor(Color.BLUE);
            emb.setTitle("Eval results");
            if (out == null) {
                emb = Commons.coolEmbedGet(ctx, "Eval", "Returned no errors", Color.BLUE);
            } else {
                emb = Commons.coolEmbedGet(ctx, "Eval", "", Color.BLUE);
                emb.setDescription("```" + out.toString() + "```");
            }
            EmbedBuilder finalEmb = emb;
            message.editMessage(emb.build()).queue(
                    message1 -> {
                        onEdit(ctx, args, event, finalEmb, message1, raw);
                    },
                    (__) -> {
                        finalEmb.setDescription("```" + "Output too large to display" + "```");
                        message.editMessage(finalEmb.build()).queue(
                                message1 -> {
                                    onEdit(ctx, args, event, finalEmb, message1, raw);
                                }
                        );
                    }
            );
        } catch (Exception e) {
            emb.setColor(Color.orange);
            emb.setTitle("Eval results");
            emb.setDescription("```" + e.getMessage() + "```");
            EmbedBuilder finalEmb1 = emb;
            message.editMessage(emb.build()).queue(
                    message1 -> {
                        onEdit(ctx, args, event, finalEmb1, message1, raw);
                    }
            );
        }
    }

    public void onEdit(CommandContext ctx, List<String> args, GuildMessageReceivedEvent event2, EmbedBuilder emb, Message message, String raw) {
        waiter.waitForEvent(
                GuildMessageUpdateEvent.class, (event) -> {
                    User user = event.getAuthor();
                    return !user.isBot() && user.equals(ctx.getAuthor()) && event.getChannel().equals(ctx.getChannel()) && event.getMessage().getId().equals(ctx.getMessage().getId());
                },
                (event) -> {


                    if (!event.getAuthor().getId().equals(Configuration.get("OWNER_ID"))) {
                        eval(ctx, args, ctx.getEvent(), emb, message, event.getMessage().getContentRaw(), waiter, manager);
                    }


                },
                3, TimeUnit.MINUTES,
                () -> {
                    try {

                    } catch (Exception e) {

                    }
                }
        );
    }

}
