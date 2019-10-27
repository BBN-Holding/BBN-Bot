package com.bbn.BBNBot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;

public class MemberLeaveListener extends ListenerAdapter {
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        if (!event.getMember().getUser().isBot()) {
            if (event.getMember().getUser().getAvatarId() == null) {
                event.getGuild().getTextChannelsByName("log", true).get(0).sendMessage(new EmbedBuilder().setTitle("User left").setAuthor(event.getMember().getUser().getAsTag(), "https://canary.discordapp.com/channels/@me/" + event.getMember().getId(), event.getMember().getUser().getDefaultAvatarUrl()).setTimestamp(Instant.now()).setColor(Color.RED).build()).queue();
            } else {
                event.getGuild().getTextChannelsByName("log", true).get(0).sendMessage(new EmbedBuilder().setTitle("User left").setAuthor(event.getMember().getUser().getAsTag(), "https://canary.discordapp.com/channels/@me/" + event.getMember().getId(), event.getMember().getUser().getAvatarUrl()).setTimestamp(Instant.now()).setColor(Color.RED).build()).queue();
            }

        } else {
           if (event.getMember().getUser().getAvatarId() == null) {
                event.getGuild().getTextChannelsByName("log", true).get(0).sendMessage(new EmbedBuilder().setTitle("Bot left").setAuthor(event.getMember().getUser().getAsTag(), "https://canary.discordapp.com/channels/@me/" + event.getMember().getId(), event.getMember().getUser().getDefaultAvatarUrl()).setTimestamp(Instant.now()).setColor(Color.RED).build()).queue();
            } else {
                event.getGuild().getTextChannelsByName("log", true).get(0).sendMessage(new EmbedBuilder().setTitle("Bot left").setAuthor(event.getMember().getUser().getAsTag(), "https://canary.discordapp.com/channels/@me/" + event.getMember().getId(), event.getMember().getUser().getAvatarUrl()).setTimestamp(Instant.now()).setColor(Color.RED).build()).queue();
            }
        }
    }
}
