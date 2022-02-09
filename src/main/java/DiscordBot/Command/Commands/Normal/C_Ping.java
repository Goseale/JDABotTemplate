package DiscordBot.Command.Commands.Normal;

import DiscordBot.Command.CommandContext;
import DiscordBot.Command.ICommand;
import DiscordBot.CommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;

public class C_Ping implements ICommand {

    private int hours;
    private int minutes;
    private int seconds;
    private int milisec;

    private int actualH;
    private int actualM;
    private int actualS;
    private int actualMS;

    private int msActual;
    private int msTime;

    @Override
    public void handle(CommandContext ctx) {
        CommandManager.addCooldown(ctx,getName());
        TextChannel channel = ctx.getChannel();
        JDA jda = ctx.getJDA();
        EmbedBuilder emb = new EmbedBuilder();
        emb.setColor(Color.yellow);
        emb.setTitle("PONG!");
        emb.setDescription("```\n" +
                "Gateway ping : ["+ "█████" +"ms]\n" +
                "Discord ping : ["+ "█████" +"ms]\n" +
                "Rest ping    : ["+ "█████" +"ms]\n" +
                "```");
        OffsetDateTime oMessageTime = ctx.getEvent().getMessage().getTimeCreated();

        channel.sendMessage(emb.build()).queue(
                (message) -> {

                    OffsetDateTime msg = message.getTimeCreated();

                    long delay = msg.getLong(ChronoField.MILLI_OF_DAY) - oMessageTime.getLong(ChronoField.MILLI_OF_DAY);

                    final String[] restPing = {""};
                    final long[] gtwPing = {0};
                    jda.getRestPing().queue(
                            (ping) -> {
                                restPing[0] = ping+"";

                                gtwPing[0] = jda.getGatewayPing();
                                emb.setColor(Color.cyan);
                                emb.setTitle("Ping results");
                                int digits = 5;
                                emb.setDescription("```css\n" +
                                        "Gateway ping : ["+ formatN(String.valueOf(gtwPing[0]),digits) +"ms]\n" +
                                        "Discord ping : ["+ formatN(String.valueOf(delay),digits)+"ms]\n" +
                                        "Rest ping    : ["+ formatN(restPing[0],digits)+"ms]\n" +
                                        "```");
                                message.editMessage(emb.build()).delay(Duration.ofSeconds(1)).queue(
                                        (message1) -> {message1.editMessage(emb.build()).queue();}
                                );

                            }
                    );

                }
        );
    }

    @Override
    public String getName() {
        return "ping";
    }

    public String getHelp() {
        return "Shows the current ping to the discord servers";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("latency", "pong");
    }

    @Override
    public int getCooldown() {
        return 3000;
    }

    public String formatN(String count, int digits) {
        String var = ""+count;
        for (int i = 0; i < digits; i++) {
            var+=" ";
        }
        var = var.substring(0,digits);
        return var;
    }
}
