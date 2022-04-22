package DiscordBot.Command.Slash.Normal;

import DiscordBot.Command.ICommandSlash;
import DiscordBot.Util.Commons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;

public class SC_Ping implements ICommandSlash {
    @Override
    public void handle(SlashCommandEvent event) {

        JDA jda = event.getJDA();
        EmbedBuilder emb = new EmbedBuilder();
        emb.setColor(Color.yellow);
        emb.setTitle("PONG!");
        emb.setDescription("```\n" +
                "Gateway ping : ["+ "█████" +"ms]\n" +
                "Discord ping : ["+ "█████" +"ms]\n" +
                "Rest ping    : ["+ "█████" +"ms]\n" +
                "```");
        OffsetDateTime oMessageTime = event.getTimeCreated();

        event.replyEmbeds(emb.build()).queue(
                (message) -> {

                    OffsetDateTime msg = OffsetDateTime.now();

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
                                message.editOriginalEmbeds(emb.build()).queue();

                            }
                    );

                }
        );

    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getHelp() {
        return "Description";
    }

    @Override
    public CommandData getSlash() {
        return new CommandData(this.getName(), "Gets a responce from the bot");
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
