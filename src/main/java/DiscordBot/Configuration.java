package DiscordBot;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import org.json.JSONObject;

import java.io.*;

public class Configuration {

    public static String get(String key) {
        StackTraceElement[] str = Thread.currentThread().getStackTrace();
        StackTraceElement stack = str[str.length-1];
        String requester = stack.getFileName()+":"+stack.getLineNumber();
        System.out.println("[ENV] Retrieving ("+key+") for ("+requester+")");
        if (System.getenv(key.toUpperCase()) != null) {
            System.out.println("[ENV] Success");
            return System.getenv(key.toUpperCase());
        } else {
            System.out.println("[ENV] System var: ("+key+") not found. Attempting .env");
            String d = null;
            try {
                Dotenv dotenv = Dotenv.load();
                d = dotenv.get(key.toUpperCase());
                System.out.println("[ENV] Success");
            } catch (Exception e) {
                System.out.println("[ENV] Failed. Returning blank");
                return "";
            }
            return d;
        }
    }


}
