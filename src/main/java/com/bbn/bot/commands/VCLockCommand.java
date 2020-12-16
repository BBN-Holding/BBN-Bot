package com.bbn.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class VCLockCommand implements Command {
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
                event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("Voicelocker").setDescription("").build()).queue(
                        msg -> {
                            msg.addReaction(event.getGuild().getEmoteById("780057996712476703")).queue();
                            msg.addReaction(event.getGuild().getEmoteById("780057870573109258")).queue();
                            msg.addReaction("\uD83D\uDED1").queue();
                        }
                );
        }
    }
}
