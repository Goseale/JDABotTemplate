package DiscordBot.Command.Slash.Owner;

import DiscordBot.Command.ICommandSlash;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class SC_DummyCommand implements ICommandSlash {
    @Override
    public void handle(SlashCommandInteractionEvent event) {
        event.getChannel().sendMessage("Test from eval").queue();
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public CommandData getSlash() {
        return Commands.slash(this.getName(), "This is an example command. Useful for when you need assistance in copy pasting code intro /eval");
    }
}
