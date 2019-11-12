package com.bbn.BBNBot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;

public class ReactionAddListener extends ListenerAdapter {

    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getChannel().getId().equals("449267564745588737")) {
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("448554734312226847")).reason("Verified").queue();
            event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById("636950878615502849")).reason("Verified").queue();
            event.getReaction().removeReaction(event.getUser()).queue();
            if (event.getMember().getUser().getAvatarId() == null) {
                event.getGuild().getTextChannelsByName("log", true).get(0).sendMessage(new EmbedBuilder()
                        .setTitle("User verified")
                        .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getDefaultAvatarUrl(), event.getMember().getUser().getDefaultAvatarUrl())
                        .setTimestamp(Instant.now())
                        .setColor(Color.GREEN)
                        .build()).queue();
            } else {
                event.getGuild().getTextChannelsByName("log", true).get(0).sendMessage(new EmbedBuilder()
                        .setTitle("User verified")
                        .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                        .setTimestamp(Instant.now())
                        .setColor(Color.GREEN)
                        .build()).queue();
            }
        }
    }
}