package com.bbn.BBNBot.listeners;

import com.bbn.BBNBot.util.SECRETS;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.kohsuke.github.GHContentUpdateResponse;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class MessageReceiveListener extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getChannel().getId().equals("449267564745588737")) {
            if (event.getMessage().getContentRaw().toLowerCase().contains("community")) {
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("448554734312226847")).reason("Verified").queue();
                event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById("636950878615502849")).reason("Verified").queue();
                event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
                if (event.getMember().getUser().getAvatarId() == null) {
                    event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                            .setTitle("User verified")
                            .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getDefaultAvatarUrl(), event.getMember().getUser().getDefaultAvatarUrl())
                            .addField("User Creation Time", event.getMember().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                            .addField("ID", event.getMember().getId(), true)
                            .setTimestamp(Instant.now())
                            .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                            .setColor(Color.GREEN)
                            .build()).queue();
                } else {
                    event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                            .setTitle("User verified")
                            .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                            .addField("User Creation Time", event.getMember().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                            .addField("ID", event.getMember().getId(), true)
                            .setTimestamp(Instant.now())
                            .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                            .setColor(Color.GREEN)
                            .build()).queue();
                }
            } else {
                event.getMessage().delete().queueAfter(1, TimeUnit.SECONDS);
            }
        } else if (event.getMessage().getContentRaw().startsWith(event.getGuild().getSelfMember().getAsMention() + " close") && event.getChannel().getParent().equals(event.getGuild().getCategoryById("648518640718839829"))) {
            TextChannel channel = event.getGuild().getTextChannelsByName(event.getMember().getUser().getName(), true).get(0);
                if (channel.getTopic().contains(event.getAuthor().getId()) || event.getAuthor().getId().equals("477141528981012511") || event.getAuthor().getId().equals("261083609148948488")) {
                    channel.getManager().setParent(event.getGuild().getCategoryById("639550970812039177")).reason("Case closed").queue();
                    channel.getManager().setName(channel.getName() + "-archive").queue();
                    channel.getManager().removePermissionOverride(event.getMember()).queue();
                    event.getMessage().addReaction("✅").queue();
                    try {
                        GitHub connection = GitHub.connectUsingOAuth(SECRETS.GHTOKEN);
                        GHRepository Mining = connection.getMyself().getRepository("Data-Mining");
                        GHContentUpdateResponse commit = Mining.createContent().branch("master").path(event.getMember().getUser().getAsTag()).content(event.getChannel().getHistory().toString()).message("Message").commit();
                        event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                                .setTitle("Log file created")
                                .setDescription("[Successfully create the log file on GitHub](" + commit.getCommit().getHtmlUrl() + ")")
                                .setTimestamp(Instant.now())
                                .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                                .setColor(Color.GREEN)
                                .build()).queue();
                    } catch (IOException e) {
                        event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                                .setTitle("Error while creating")
                                .setDescription("Error while creating the GitHub log file.")
                                .setTimestamp(Instant.now())
                                .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                                .setColor(Color.RED)
                                .build()).queue();
                    }
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Not you channel")
                            .setDescription("You can only execute this command in your own channel.")
                            .setTimestamp(Instant.now())
                            .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                            .setColor(Color.RED)
                            .build()).queue();
                }
        }
    }
}
