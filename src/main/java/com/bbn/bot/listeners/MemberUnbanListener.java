package com.bbn.bot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class MemberUnbanListener extends ListenerAdapter {

    @Override
    public void onGuildUnban(@Nonnull GuildUnbanEvent event) {
        if (!event.getUser().isBot()) {
            if (event.getUser().getAvatarId() == null) {
                event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                        .setTitle("User unbanned")
                        .setAuthor(event.getUser().getAsTag(), event.getUser().getDefaultAvatarUrl(), event.getUser().getDefaultAvatarUrl())
                        .addField("User Creation Time", event.getUser().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                        .addField("ID", event.getUser().getId(), true)
                        .setTimestamp(Instant.now())
                        .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                        .setColor(Color.RED)
                        .build()).queue();
            } else {
                event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                        .setTitle("User unbanned")
                        .setAuthor(event.getUser().getAsTag(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl())
                        .addField("User Creation Time", event.getUser().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                        .addField("ID", event.getUser().getId(), true)
                        .setTimestamp(Instant.now())
                        .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                        .setColor(Color.RED)
                        .build()).queue();
            }
        } else {
            if (event.getUser().getAvatarId() == null) {
                event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                        .setTitle("Bot unbanned")
                        .setAuthor(event.getUser().getAsTag(), event.getUser().getDefaultAvatarUrl(), event.getUser().getDefaultAvatarUrl())
                        .addField("Bot Creation Time", event.getUser().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                        .addField("ID", event.getUser().getId(), true)
                        .setTimestamp(Instant.now())
                        .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                        .setColor(Color.RED)
                        .build()).queue();
            } else {
                event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                        .setTitle("Bot unbanned")
                        .setAuthor(event.getUser().getAsTag(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl())
                        .addField("Bot Creation Time", event.getUser().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                        .addField("ID", event.getUser().getId(), true)
                        .setTimestamp(Instant.now())
                        .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                        .setColor(Color.RED)
                        .build()).queue();
            }
        }
        super.onGuildUnban(event);
    }
}
