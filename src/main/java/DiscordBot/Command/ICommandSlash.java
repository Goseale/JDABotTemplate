package DiscordBot.Command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface ICommandSlash {

    void handle(SlashCommandInteractionEvent event);

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
