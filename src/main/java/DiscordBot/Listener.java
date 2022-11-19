package DiscordBot;

import DiscordBot.Events.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.duncte123.botcommons.BotCommons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Listener extends ListenerAdapter {
    private CommandManager manager;

    public Listener(EventWaiter waiter, JDA jda) {
        final CommandManager manager = new CommandManager(waiter, jda);
        jda.addEventListener(new SlashCommand(manager));
        this.manager = manager;
    }


    @Override
    public void onReady(ReadyEvent event) {
        System.out.println();
        System.out.println("-----Running start script-----");

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        Random rnd = new Random();
        int numberChosen = rnd.nextInt(10000);

        event.getJDA().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        event.getJDA().getPresence().setActivity(Activity.watching("Loading the bot..."));

        loadConfiguration(event.getJDA());

        int userCount = 0;
        for (Guild guild : event.getJDA().getGuilds()) {
            userCount += guild.getMemberCount();
        }

        event.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
        event.getJDA().getPresence().setActivity(Activity.watching(userCount+" users"));

        //event.getJDA().getPresence().setActivity(Activity.streaming("Default prefix " + Configuration.get("prefix") + " | Rng:" + numberChosen, "https://twitch.tv/goseale"));

        System.out.println("\n\n");
        System.out.println("-----Start script  ended-----");
        System.out.println("Logged in as: " + event.getJDA().getSelfUser().getAsTag());
        System.out.println("\n");
        System.out.println("!BOT READY AND FUNCTIONAL!\n");
    }

    public String getTime(int numero) {
        int minutes = (int) Math.floor(numero / 60);
        String helper = "0";
        int seconds = numero % 60;
        if (seconds > 9) {
            helper = "";
        }

        return minutes + ":" + helper + seconds;
    }

    public String formatN(double count, int digits) {
        String var = "" + count;
        for (int i = 0; i < digits; i++) {
            var += " ";
        }
        var = var.substring(0, digits);
        return var;
    }

    public String formatServerName(String named) {
        String var = named;
        for (int i = 0; i < 16; i++) {
            var += " ";
        }
        var = var.substring(0, 16);
        return var;
    }


    @Override
    public void onShutdown(@Nonnull ShutdownEvent event) {
        System.out.println(":::[ THE BOT IS SHUTTING DOWN ]:::");
        BotCommons.shutdown(event.getJDA());
        event.getJDA().getPresence().setStatus(OnlineStatus.OFFLINE);
        System.out.println("---[   THE BOT HAS SHUTDOWN   ]---");
        System.exit(0);
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        final String guildID = event.getGuild().getId();
        String prefix = "<@!"+event.getJDA().getSelfUser().getId()+">";
        String raw = event.getMessage().getContentRaw();
        User user = event.getAuthor();

        if (user.isBot() || event.isWebhookMessage()) {
            return;
        }

        if (raw.trim().equalsIgnoreCase("<@!"+event.getJDA().getSelfUser().getId()+">")) {
            EmbedBuilder emb = new EmbedBuilder();
            emb.setTitle("Hi " + event.getAuthor().getName());
            emb.setDescription("The prefix for this bot is / (Slash Command)`");
            emb.setColor(Color.BLUE);
            emb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
            try {
                event.getChannel().sendMessageEmbeds(emb.build()).queue();
            } catch (Exception e) {

            }

        }

    }

    public static void loadConfiguration(JDA jda) {
        System.out.println("Loading configuration...");
        JSONObject jsonConfig = Configuration.getJsonConfig(jda);
        if (!jsonConfig.has("guilds")) {
            JSONObject json = new JSONObject();
            jsonConfig.put("guilds",json);
        }
        Configuration.setJsonConfig(jsonConfig);
        System.out.println("Config loaded");
    }


}
