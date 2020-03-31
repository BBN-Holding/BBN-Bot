package com.bbn.bot.commands;

import com.bbn.bot.BBNBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.kohsuke.github.GHContentUpdateResponse;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class CloseCommand implements Command {

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (event.getTextChannel().getParent().equals(event.getGuild().getCategoryById("648518640718839829"))) {
            TextChannel channel = event.getGuild().getTextChannelsByName(event.getChannel().getName(), true).get(0);
            if (channel.getTopic().contains(event.getAuthor().getId()) || event.getAuthor().getId().equals("477141528981012511") || event.getAuthor().getId().equals("261083609148948488")) {
                channel.getManager().setParent(event.getGuild().getCategoryById("639550970812039177")).reason("Case closed").queue();
                channel.getManager().removePermissionOverride(event.getMember()).queue();
                event.getMessage().addReaction("✅").queue();
                channel.getManager().setName(channel.getName() + "-archive").queue();
                try {
                    GitHub connection = GitHub.connectUsingOAuth(BBNBot.config.getGitHubToken());
                    GHRepository Mining = connection.getMyself().getRepository("Data-Mining");
                    String pattern = "dd-MM-yyyy";
                    String date = new SimpleDateFormat(pattern).format(new Date());
                    GHContentUpdateResponse commit = Mining.createContent().branch("master")
                            .path(date + "/" + channel.getId() + ".md")
                            .content(channel.getHistory().retrievePast(100).complete().toString())
                            .message("Channel by " + channel.getName() + " archived")
                            .commit();
                    commit.getCommit().createComment("Archived by " + event.getAuthor().getName(), channel.getId() + ".md", 1, 1);
                    event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                            .setTitle("Log file created")
                            .setDescription("[Successfully created the log file on GitHub](" + commit.getCommit().getHtmlUrl() + ")")
                            .setTimestamp(Instant.now())
                            .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                            .setColor(Color.GREEN)
                            .build()).queue();
                } catch (IOException e) {
                    event.getGuild().getTextChannelById("452789888945750046").sendMessage(new EmbedBuilder()
                            .setTitle("Error while creating")
                            .setDescription("```" + e.toString() + "```")
                            .setTimestamp(Instant.now())
                            .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                            .setColor(Color.RED)
                            .build()).queue();
                    e.printStackTrace();
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
        } else {
            event.getTextChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Invalid channel")
                    .setDescription("You have to execute this command in a customer related channel.")
                    .setTimestamp(Instant.now())
                    .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                    .setColor(Color.RED)
                    .build()).queue();
        }
    }
}
