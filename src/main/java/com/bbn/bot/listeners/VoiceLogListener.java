package com.bbn.bot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;

public class VoiceLogListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceDeafen(@Nonnull GuildVoiceDeafenEvent event) {
        TextChannel c = event.getJDA().getTextChannelById("689123407249670164");
        if (event.getVoiceState().isDeafened()) {
            c.sendMessage(new EmbedBuilder()
                    .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                    .setTitle("User deafed")
                    .addField("Channel", event.getVoiceState().getChannel().getName(), true)
                    .addField("Members in Channel", String.valueOf(event.getVoiceState().getChannel().getMembers().size()), true)
                    .setColor(Color.RED)
                    .setFooter("Provided by BBN", "https://bigbotnetwork.com/images/avatar.png")
                    .setTimestamp(Instant.now())
                    .build()).queue();
        } else {
            c.sendMessage(new EmbedBuilder()
                    .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                    .setTitle("User undeafed")
                    .addField("Channel", event.getVoiceState().getChannel().getName(), true)
                    .addField("Members in Channel", String.valueOf(event.getVoiceState().getChannel().getMembers().size()), true)
                    .setColor(Color.GREEN)
                    .setFooter("Provided by BBN", "https://bigbotnetwork.com/images/avatar.png")
                    .setTimestamp(Instant.now())
                    .build()).queue();
        }
        super.onGuildVoiceDeafen(event);
    }

    @Override
    public void onGuildVoiceMute(@Nonnull GuildVoiceMuteEvent event) {
        TextChannel c = event.getJDA().getTextChannelById("689123407249670164");
        if (event.getVoiceState().isMuted()) {
            c.sendMessage(new EmbedBuilder()
                    .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                    .setTitle("User muted")
                    .addField("Channel", event.getVoiceState().getChannel().getName(), true)
                    .addField("Members in Channel", String.valueOf(event.getVoiceState().getChannel().getMembers().size()), true)
                    .setColor(Color.RED)
                    .setFooter("Provided by BBN", "https://bigbotnetwork.com/images/avatar.png")
                    .setTimestamp(Instant.now())
                    .build()).queue();
        } else {
            c.sendMessage(new EmbedBuilder()
                    .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                    .setTitle("User unmuted")
                    .addField("Channel", event.getVoiceState().getChannel().getName(), true)
                    .addField("Members in Channel", String.valueOf(event.getVoiceState().getChannel().getMembers().size()), true)
                    .setColor(Color.GREEN)
                    .setFooter("Provided by BBN", "https://bigbotnetwork.com/images/avatar.png")
                    .setTimestamp(Instant.now())
                    .build()).queue();
        }
        super.onGuildVoiceMute(event);
    }

    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        TextChannel c = event.getJDA().getTextChannelById("689123407249670164");
        c.sendMessage(new EmbedBuilder()
                .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                .setTitle("User joined")
                .addField("Channel", event.getChannelJoined().getName(), true)
                .addField("Members in Channel", String.valueOf(event.getChannelJoined().getMembers().size()), true)
                .setColor(Color.GREEN)
                .setFooter("Provided by BBN", "https://bigbotnetwork.com/images/avatar.png")
                .setTimestamp(Instant.now())
                .build()).queue();
        super.onGuildVoiceJoin(event);
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        TextChannel c = event.getJDA().getTextChannelById("689123407249670164");
        c.sendMessage(new EmbedBuilder()
                .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                .setTitle("User left")
                .addField("Channel", event.getChannelLeft().getName(), true)
                .addField("Members in Channel", String.valueOf(event.getChannelLeft().getMembers().size()), true)
                .setColor(Color.RED)
                .setFooter("Provided by BBN", "https://bigbotnetwork.com/images/avatar.png")
                .setTimestamp(Instant.now())
                .build()).queue();
        super.onGuildVoiceLeave(event);
    }

    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        TextChannel c = event.getJDA().getTextChannelById("689123407249670164");
        c.sendMessage(new EmbedBuilder()
                .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                .setTitle("User switched channel")
                .addField("Old Channel", event.getChannelLeft().getName(), true)
                .addField("Members in old channel", String.valueOf(event.getChannelLeft().getMembers().size()), true)
                .addBlankField(true)
                .addField("New Channel", event.getChannelJoined().getName(), true)
                .addField("Members in new channel", String.valueOf(event.getChannelJoined().getMembers().size()), true)
                .setColor(Color.YELLOW)
                .setFooter("Provided by BBN", "https://bigbotnetwork.com/images/avatar.png")
                .setTimestamp(Instant.now())
                .build()).queue();
        super.onGuildVoiceMove(event);
    }
}
