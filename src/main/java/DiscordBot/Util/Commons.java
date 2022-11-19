package DiscordBot.Util;

import DiscordBot.Configuration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.List;
import java.util.*;

public class Commons {
    public static final List<String> badWords = Arrays.asList("4r5e", "5h1t", "5hit", "a55", "anal", "anus", "ar5e", "arrse", "arse", "ass", "ass-fucker", "asses", "assfucker", "assfukka", "asshole", "assholes", "asswhole", "a_s_s", "b!tch", "b00bs", "b17ch", "b1tch", "ballbag", "balls", "ballsack", "bastard", "beastial", "beastiality", "bellend", "bestial", "bestiality", "bi+ch", "biatch", "bitch", "bitcher", "bitchers", "bitches", "bitchin", "bitching", "bloody", "blow job", "blowjob", "blowjobs", "boiolas", "bollock", "bollok", "boner", "boob", "boobs", "booobs", "boooobs", "booooobs", "booooooobs", "breasts", "buceta", "bugger", "bum", "bunny fucker", "butt", "butthole", "buttmuch", "buttplug", "c0ck", "c0cksucker", "carpet muncher", "cawk", "chink", "cipa", "cl1t", "clit", "clitoris", "clits", "cnut", "cock", "cock-sucker", "cockface", "cockhead", "cockmunch", "cockmuncher", "cocks", "cocksuck", "cocksucked", "cocksucker", "cocksucking", "cocksucks", "cocksuka", "cocksukka", "cok", "cokmuncher", "coksucka", "coon", "cox", "crap", "cum", "cummer", "cumming", "cums", "cumshot", "cunilingus", "cunillingus", "cunnilingus", "cunt", "cuntlick", "cuntlicker", "cuntlicking", "cunts", "cyalis", "cyberfuc", "cyberfuck", "cyberfucked", "cyberfucker", "cyberfuckers", "cyberfucking", "d1ck", "damn", "dick", "dickhead", "dildo", "dildos", "dink", "dinks", "dirsa", "dlck", "dog-fucker", "doggin", "dogging", "donkeyribber", "doosh", "duche", "dyke", "ejaculate", "ejaculated", "ejaculates", "ejaculating", "ejaculatings", "ejaculation", "ejakulate", "f u c k", "f u c k e r", "f4nny", "fag", "fagging", "faggitt", "faggot", "faggs", "fagot", "fagots", "fags", "fanny", "fannyflaps", "fannyfucker", "fanyy", "fatass", "fcuk", "fcuker", "fcuking", "feck", "fecker", "felching", "fellate", "fellatio", "fingerfuck", "fingerfucked", "fingerfucker", "fingerfuckers", "fingerfucking", "fingerfucks", "fistfuck", "fistfucked", "fistfucker", "fistfuckers", "fistfucking", "fistfuckings", "fistfucks", "flange", "fook", "fooker", "fuck", "fucka", "fucked", "fucker", "fuckers", "fuckhead", "fuckheads", "fuckin", "fucking", "fuckings", "fuckingshitmotherfucker", "fuckme", "fucks", "fuckwhit", "fuckwit", "fudge packer", "fudgepacker", "fuk", "fuker", "fukker", "fukkin", "fuks", "fukwhit", "fukwit", "fux", "fux0r", "f_u_c_k", "gangbang", "gangbanged", "gangbangs", "gaylord", "gaysex", "goatse", "God", "god-dam", "god-damned", "goddamn", "goddamned", "hardcoresex", "hell", "heshe", "hoar", "hoare", "hoer", "homo", "hore", "horniest", "horny", "hotsex", "jack-off", "jackoff", "jap", "jerk-off", "jism", "jiz", "jizm", "jizz", "kawk", "knob", "knobead", "knobed", "knobend", "knobhead", "knobjocky", "knobjokey", "kock", "kondum", "kondums", "kum", "kummer", "kumming", "kums", "kunilingus", "l3i+ch", "l3itch", "labia", "lust", "lusting", "m0f0", "m0fo", "m45terbate", "ma5terb8", "ma5terbate", "masochist", "master-bate", "masterb8", "masterbat*", "masterbat3", "masterbate", "masterbation", "masterbations", "masturbate", "mo-fo", "mof0", "mofo", "mothafuck", "mothafucka", "mothafuckas", "mothafuckaz", "mothafucked", "mothafucker", "mothafuckers", "mothafuckin", "mothafucking", "mothafuckings", "mothafucks", "mother fucker", "motherfuck", "motherfucked", "motherfucker", "motherfuckers", "motherfuckin", "motherfucking", "motherfuckings", "motherfuckka", "motherfucks", "muff", "mutha", "muthafecker", "muthafuckker", "muther", "mutherfucker", "n1gga", "n1gger", "nazi", "nigg3r", "nigg4h", "nigga", "niggah", "niggas", "niggaz", "nigger", "niggers", "nob", "nob jokey", "nobhead", "nobjocky", "nobjokey", "numbnuts", "nutsack", "orgasim", "orgasims", "orgasm", "orgasms", "p0rn", "pawn", "pecker", "penis", "penisfucker", "phonesex", "phuck", "phuk", "phuked", "phuking", "phukked", "phukking", "phuks", "phuq", "pigfucker", "pimpis", "piss", "pissed", "pisser", "pissers", "pisses", "pissflaps", "pissin", "pissing", "pissoff", "poop", "porn", "porno", "pornography", "pornos", "prick", "pricks", "pron", "pube", "pusse", "pussi", "pussies", "pussy", "pussys", "rectum", "retard", "rimjaw", "rimming", "s hit", "s.o.b.", "sadist", "schlong", "screwing", "scroat", "scrote", "scrotum", "semen", "sex", "sh!+", "sh!t", "sh1t", "shag", "shagger", "shaggin", "shagging", "shemale", "shi+", "shit", "shitdick", "shite", "shited", "shitey", "shitfuck", "shitfull", "shithead", "shiting", "shitings", "shits", "shitted", "shitter", "shitters", "shitting", "shittings", "shitty", "skank", "slut", "sluts", "smegma", "smut", "snatch", "son-of-a-bitch", "spac", "spunk", "s_h_i_t", "t1tt1e5", "t1tties", "teets", "teez", "testical", "testicle", "tit", "titfuck", "tits", "titt", "tittie5", "tittiefucker", "titties", "tittyfuck", "tittywank", "titwank", "tosser", "turd", "tw4t", "twat", "twathead", "twatty", "twunt", "twunter", "v14gra", "v1gra", "vagina", "viagra", "vulva", "w00se", "wang", "wank", "wanker", "wanky", "whoar", "whore", "willies", "willy", "xrated", "xxx", "muthafucka");
    public static String dateFormat = "dd-MM-yyyy hh:mm:ss aa";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);


    public Commons() {
    }

    public static String getAsString(List<String> args) {
        return String.join(" ", args.subList(0, args.size()));
    }

    public static String trimToMessage(String string) {
        if (string.length() > 2000) {
            return string.substring(0, 2000);
        } else {
            return string;
        }
    }

    public static String trimToInt(String string, int trimTo) {
        if (string.length() > trimTo) {
            return string.substring(0, trimTo);
        } else {
            return string;
        }
    }

    public static String trimToEmbedDescription(String string) {
        if (string.length() > 1000) {
            return string.substring(0, 1000);
        } else {
            return string;
        }
    }

    public static String trimToEmbedTitle(String string) {
        if (string.length() > 500) {
            return string.substring(0, 500);
        } else {
            return string;
        }
    }

    public static String getAsMarkdown(String text, String link) {
        return "[" + text + "](" + link + ")";
    }

    public static String getQuality2048(String url) {

        url = (url.substring(0, url.length() - 3) + "png?size=2048");
        return url;

    }

    public static String getQuality512(String url) {

        url = (url.substring(0, url.length() - 3) + "png?size=512");
        return url;

    }

    public static String getQuality256(String url) {

        url = (url.substring(0, url.length() - 3) + "png?size=256");
        return url;

    }

    public static String getAsString(List<String> args, int getFrom) {
        return String.join(" ", args.subList(getFrom, args.size()));
    }

    public static String getAsUrlEncoded(List<String> args) throws UnsupportedEncodingException {
        return URLEncoder.encode(getAsString(args), StandardCharsets.UTF_8.name());
    }

    public static String getAsStringList(List<String> args) {
        return String.join("\n", args.subList(0, args.size()));
    }

    public static boolean hasBadWord(String string) {

        final List<String> split = Arrays.asList(string.toLowerCase().split(" "));
        for (String badWord : badWords) {
            if (split.contains(badWord)) {
                return true;
            }
        }
        return false;

    }

    public static String replaceBadWord(String string) {

        final List<String> split = Arrays.asList(string.split(" "));
        for (String badWord : badWords) {
            int count = 0;
            for (String str : split) {
                if (str.toLowerCase().equalsIgnoreCase(badWord)) {
                    split.set(count, "[Redacted] ");
                }
                count++;
            }
        }
        return Commons.getAsString(split);

    }

    public static String forceReplace(String Original, String textToReplace, String replacement) {

        String r = "";
        for (String s : Original.split(" ")) {
            if (s.toLowerCase().contains(textToReplace.toLowerCase())) {
                r += replacement + " ";
            } else {
                r += s + " ";
            }
        }
        return "";

    }

    public static InputStream getNetResource(String urlOfResource) throws IOException {
        URL url = null;
        try {
            url = new URL(urlOfResource);
        } catch (MalformedURLException e) {

        }
        URLConnection openConnection = null;
        try {
            openConnection = url.openConnection();
        } catch (IOException e) {

        }
        openConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        openConnection.setReadTimeout(10000);
        openConnection.setConnectTimeout(10000);
        try {
            return openConnection.getInputStream();
        } catch (IOException e) {
            throw e;
        }
    }

    public static String convertMilliSecondsToFormattedDate(String milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return simpleDateFormat.format(calendar.getTime());
    }

    public static Object JSONgetGuildProperty(JDA jda, Guild guild, String property) {
        JSONObject jsonConfig = Configuration.getJsonConfig(jda);
        JSONObject jguilds = (JSONObject) jsonConfig.get("guilds");
        if (!jguilds.has(guild.getId())) {
            JSONObject json = new JSONObject();
            json.put(property, "");
            json.put("name", guild.getName());
            jguilds.put(guild.getId(), json);
            jsonConfig.put("guilds", jguilds);
            Configuration.setJsonConfig(jsonConfig);
            return JSONgetGuildProperty(jda, guild, property);
        } else {
            JSONObject gProperties = (JSONObject) jguilds.get((guild.getId()));
            if (!gProperties.has(property)) {
                gProperties.put("name", guild.getName());
                gProperties.put(property, "");
                jguilds.put(guild.getId(), gProperties);
                jsonConfig.put("guilds", jguilds);
                Configuration.setJsonConfig(jsonConfig);
                return "";
            } else {
                return gProperties.get(property);
            }
        }
    }

    public static void JSONsetGuildProperty(JDA jda, Guild guild, String property, Object value) {
        JSONObject jsonConfig = Configuration.getJsonConfig(jda);
        JSONObject jguilds = (JSONObject) jsonConfig.get("guilds");
        if (!jguilds.has(guild.getId())) {
            JSONObject json = new JSONObject();
            json.put(property, value);
            json.put("name", guild.getName());
            jguilds.put(guild.getId(), json);
            jsonConfig.put("guilds", jguilds);
            Configuration.setJsonConfig(jsonConfig);
            return;
        } else {
            JSONObject gProperties = (JSONObject) jguilds.get((guild.getId()));
            gProperties.put(property, value);
            gProperties.put("name", guild.getName());
            jguilds.put(guild.getId(), gProperties);
            jsonConfig.put("guilds", jguilds);
            Configuration.setJsonConfig(jsonConfig);
            return;
        }
    }
    public static EmbedBuilder coolEmbedGet(JDA jda, String title, String message, Color color) {
        EmbedBuilder emb = new EmbedBuilder();
        emb.setColor(color);
        emb.setAuthor(title, jda.getSelfUser().getAvatarUrl(), jda.getSelfUser().getAvatarUrl());
        emb.setDescription("> "+message);
        return emb;
    }
    public static EmbedBuilder coolEmbedGet(SlashCommandInteractionEvent ctx, String title, String message, Color color) {
        EmbedBuilder emb = new EmbedBuilder();
        emb.setColor(color);
        emb.setAuthor(title, ctx.getJDA().getSelfUser().getAvatarUrl(), ctx.getJDA().getSelfUser().getAvatarUrl());
        emb.setDescription("> "+message);
        return emb;
    }
}
