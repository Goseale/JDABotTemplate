package DiscordBot;

import DiscordBot.Util.BOT_MONITOR;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.EventListener;

public class Bot implements EventListener {
    private static JDA jda;

    public static void main(String[] args) {

        EventWaiter waiter = new EventWaiter();

        ChunkingFilter chkF = ChunkingFilter.NONE;
        MemberCachePolicy none = MemberCachePolicy.VOICE;
        System.out.println("Starting and logging in...");
        try {
                jda = JDABuilder.createDefault(
                        Configuration.get("token"),
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_BANS,
                        GatewayIntent.GUILD_EMOJIS,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_PRESENCES
                )
                        //.disableCache(CacheFlag.MEMBER_OVERRIDES)
                        //.setChunkingFilter(chkF)
                        //.setMemberCachePolicy(none)
                        .enableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS)
                        .build();
                System.out.println("Logged in as "+jda.getSelfUser().getAsTag());
            } catch (Exception e) {
                System.out.println("Error while logging in\n" + e.getMessage());
                

            return;
            }

        System.out.println("Adding events...");
        jda.addEventListener(waiter);
        jda.addEventListener(new Listener(waiter,jda));
        new BOT_MONITOR(jda);
    }

}

