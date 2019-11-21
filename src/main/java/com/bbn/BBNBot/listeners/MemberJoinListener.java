package com.bbn.BBNBot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class MemberJoinListener extends ListenerAdapter {
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getMember().getUser().isBot()) {
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("636950878615502849")).reason("Auto User Role onJoin").queue();

            if (event.getMember().getUser().getAvatarId() == null) {
                event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                        .setTitle("User joined")
                        .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getDefaultAvatarUrl(), event.getMember().getUser().getDefaultAvatarUrl())
                        .addField("User Creation Time", event.getMember().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                        .setTimestamp(Instant.now())
                        .setColor(Color.YELLOW)
                        .build()).queue();
            } else {
                event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                        .setTitle("User joined")
                        .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                        .addField("User Creation Time", event.getMember().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                        .setTimestamp(Instant.now())
                        .setColor(Color.YELLOW)
                        .build()).queue();
            }

        } else {
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("645889819024687105")).reason("Auto Bot Role onJoin").queue();

            if (event.getMember().getUser().getAvatarId() == null) {
                event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                        .setTitle("Bot joined")
                        .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getDefaultAvatarUrl(), event.getMember().getUser().getDefaultAvatarUrl())
                        .addField("Bot Creation Time", event.getMember().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                        .setTimestamp(Instant.now())
                        .setColor(Color.YELLOW)
                        .build()).queue();
            } else {
                event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                        .setTitle("Bot joined")
                        .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                        .addField("Bot Creation Time", event.getMember().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                        .setTimestamp(Instant.now())
                        .setColor(Color.YELLOW)
                        .build()).queue();
            }
        }
    }
}
