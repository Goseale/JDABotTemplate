package DiscordBot.Command.Slash.Owner;

import DiscordBot.Command.ICommandSlash;
import DiscordBot.CommandManager;
import DiscordBot.Configuration;
import DiscordBot.Slash;
import DiscordBot.Util.Values;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class SC_UpdateSlashes implements ICommandSlash {
    private final CommandManager cmm;
    private final JDA jda;

    public SC_UpdateSlashes(JDA jda, CommandManager commandManager) {
        this.jda = jda;
        this.cmm = commandManager;
    }

    @Override
    public void handle(SlashCommandEvent event) {

        if (!event.getUser().getId().equals(Configuration.get("OWNER_ID"))) {
            event.reply("This is a dev only command.").setEphemeral(true).queue();
        }

        event.deferReply(true).queue(
                (__) -> {
                    Slash.updateCommands(jda,cmm, __);
                }
        );
    }

    @Override
    public String getName() {
        return "update_slashes";
    }

    @Override
    public String getHelp() {
        return "Dev command.";
    }

    @Override
    public String getCategory() {
        return "Owner";
    }

    @Override
    public CommandData getSlash() {
        return new CommandData(this.getName(), "Makes the bot perform a slash command update");
    }
}
