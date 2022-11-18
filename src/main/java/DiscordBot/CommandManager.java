package DiscordBot;

import DiscordBot.Command.CommandContext;
import DiscordBot.Command.ICommandSlash;
import DiscordBot.Command.Slash.Normal.SC_Help;
import DiscordBot.Command.Slash.Normal.SC_Ping;
import DiscordBot.Command.Slash.Owner.SC_UpdateSlashes;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    public final static List<ICommandSlash> slashCommands = new ArrayList<ICommandSlash>();

    public static List<String> cooldown = new ArrayList<>();
    public static List<String> blockedCMD = new ArrayList<>();

    public static String getPrefix = null;

    public CommandManager(EventWaiter waiter, JDA jda) {

        addSlashCommand(new SC_UpdateSlashes(jda, this));
        addSlashCommand(new SC_Help(this,waiter));
        addSlashCommand(new SC_Ping());

        //Uncomment the line below on the first run to register the slash commands
        Slash.updateCommands(jda,this);

    }

    private void addSlashCommand(ICommandSlash cmd) {
        boolean nameFound = this.slashCommands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound) {
            System.out.println("===[ COMMAND WITH SAME NAME FOUND! ]===");
            System.out.println(cmd.getName());
            System.out.println("=======================================");
            throw new IllegalArgumentException("A command with this name is already present");
        }

        slashCommands.add(cmd);
    }

    public List<ICommandSlash> getSlashCommands() {return slashCommands;}

    public static void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                System.err.println(e);
            }
        }).start();
    }

    public static void addCooldown(CommandContext ctx, int timeInMS, String command) {
        cooldown.add(ctx.getAuthor().getId() + command);
        setTimeout(() -> cooldown.remove(ctx.getAuthor().getId() + command), timeInMS);
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}