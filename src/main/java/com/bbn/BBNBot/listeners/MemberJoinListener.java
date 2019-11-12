package com.bbn.BBNBot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;

public class MemberJoinListener extends ListenerAdapter {
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getMember().getUser().isBot()) {
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("636950878615502849")).reason("Auto User Role onJoin").queue();

            if (event.getMember().getUser().getAvatarId() == null) {
                event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                        .setTitle("User joined")
                        .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getDefaultAvatarUrl(), event.getMember().getUser().getDefaultAvatarUrl())
                        .setTimestamp(Instant.now())
                        .setColor(Color.YELLOW)
                        .build()).queue();
            } else {
                event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                        .setTitle("User joined")
                        .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                        .setTimestamp(Instant.now())
                        .setColor(Color.YELLOW)
                        .build()).queue();
            }

        } else {
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("449265354691313676")).reason("Auto Bot Role onJoin").queue();

            if (event.getMember().getUser().getAvatarId() == null) {
                event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                        .setTitle("Bot joined")
                        .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getDefaultAvatarUrl(), event.getMember().getUser().getDefaultAvatarUrl())
                        .setTimestamp(Instant.now())
                        .setColor(Color.YELLOW)
                        .build()).queue();
            } else {
                event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                        .setTitle("Bot joined")
                        .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                        .setTimestamp(Instant.now())
                        .setColor(Color.YELLOW)
                        .build()).queue();
            }
        }
    }
}
