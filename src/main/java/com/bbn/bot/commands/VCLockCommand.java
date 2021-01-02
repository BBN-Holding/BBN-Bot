package com.bbn.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class VCLockCommand implements Command {
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("Voicelocker")
                    .setDescription(event.getGuild().getEmoteById("780057996712476703").getAsMention() + " - Netflix Mode\n\uD83C\uDF1B - Sleep Mode\n\uD83D\uDED1 - Reset").build()).queue(
                    msg -> {
                        msg.addReaction(event.getGuild().getEmoteById("780057996712476703")).queue();
                        msg.addReaction("\uD83C\uDF1B").queue();
                        msg.addReaction("\uD83D\uDED1").queue();
                    }
            );
        }
    }
}
