package com.bbn.bot.commands;

import com.bbn.bot.BBNBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.kohsuke.github.*;

import java.awt.*;
import java.time.Instant;

public class MergeCommand implements Command {

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (event.getAuthor().getId().equals("477141528981012511") || event.getAuthor().getId().equals("261083609148948488")) {
            try {
                GitHub connection = GitHub.connectUsingOAuth(BBNBot.config.getGitHubToken());
                GHOrganization BBN = connection.getOrganization("BigBotNetwork");
                GHRepository Hadder = BBN.getRepository("Hadder");
                GHPullRequest pr = Hadder.createPullRequest("Merge branches by BBNBot", args[0], args[1], "Pull Request created by " + event.getAuthor().getAsTag());
                pr.merge("Merged!");
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Successfully created")
                        .setDescription("[Successfully created the PR on GitHub](" + pr.getHtmlUrl() + ")")
                        .setTimestamp(Instant.now())
                        .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                        .setColor(Color.GREEN)
                        .build()).queue();
            } catch (HttpException e) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Invalid branch")
                        .setDescription("One or both branch names are invalid.")
                        .setTimestamp(Instant.now())
                        .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                        .setColor(Color.RED)
                        .build()).queue();
            } catch (Exception e) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error while creating")
                        .setDescription("```" + e.toString() + "```")
                        .setTimestamp(Instant.now())
                        .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                        .setColor(Color.RED)
                        .build()).queue();
            }
        } else {
            event.getTextChannel().sendMessage(new EmbedBuilder()
                    .setTitle("No permission")
                    .setDescription("You aren't allowed to use this command.")
                    .setTimestamp(Instant.now())
                    .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                    .setColor(Color.RED)
                    .build()).queue();
        }
    }
}
