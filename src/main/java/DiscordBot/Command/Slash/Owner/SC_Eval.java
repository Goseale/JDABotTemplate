package DiscordBot.Command.Slash.Owner;

import DiscordBot.Command.ICommandSlash;
import DiscordBot.CommandManager;
import DiscordBot.Events.eval.modalHandler;
import DiscordBot.Util.Commons;
import DiscordBot.Util.Values;
import groovy.lang.GroovyShell;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class SC_Eval implements ICommandSlash {
    private final CommandManager manager;

    public SC_Eval(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(SlashCommandInteractionEvent ctx) {
        Member member = ctx.getMember();
        EmbedBuilder emb = new EmbedBuilder();
        if (!ctx.getUser().getId().equals(Values.OWNER_ID)) {
            emb.setColor(Color.red);
            emb.setTitle("Oops");
            emb.setDescription("It looks like you are lacking the ownership of this bot\n" +
                    "When you get that, try this command again");
            ctx.replyEmbeds(emb.build()).queue();
            return;
        }

        Modal.Builder modal = Modal.create(ctx.getUser().getId()+":Eval", "Eval");
        TextInput TIcode = TextInput.create("code", "What to execute in the bot", TextInputStyle.PARAGRAPH).build();
        modal.addActionRow(TIcode);

        ctx.replyModal(modal.build()).queue(
                (__) -> {
                    new modalHandler(ctx, emb, manager);
                }
        );

    }

    @Override
    public String getName() {
        return "eval";
    }

    @Override
    public String getHelp() {
        return "Owner Only | Runs java code in the bot";
    }

    @Override
    public CommandDataImpl getSlash() {
        CommandDataImpl cmd = new CommandDataImpl(this.getName(), this.getHelp());
        return cmd;
    }

}
