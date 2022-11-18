package DiscordBot.Command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public interface ICommandSlash {

    void handle(SlashCommandEvent event);

    String getName();
    String getHelp();

    default int getCooldown() {
        return 0;
    }
    default String getCategory() {return "Normal";}
    default CommandData getSlash() {return null;}
    default Boolean isHidden() {return false;}
    default Boolean isDisabled() {return false;}
}
