package DiscordBot.Command;

import java.util.Arrays;
import java.util.List;

public interface ICommand {

    void handle(CommandContext ctx);

    String getName();
    String getHelp();

    default List<String> getAliases() {
        return Arrays.asList();
    }
    default int getCooldown() {
        return 0;
    }
    default String getCategory() {return "Normal";}

}
