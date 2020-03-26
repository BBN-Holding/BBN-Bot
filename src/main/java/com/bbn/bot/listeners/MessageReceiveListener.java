/*
 * Copyright 2018-2020 GregTCLTK and Schlauer-Hax
 *
 * Licensed under the MIT License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bbn.bot.listeners;

import com.bbn.bot.BBNBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.kohsuke.github.*;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
        } else if (event.getMessage().getContentRaw().startsWith(event.getGuild().getSelfMember().getAsMention().replace("@", "@!") + " close") || event.getMessage().getContentRaw().startsWith("bbn!close") && event.getChannel().getParent().equals(event.getGuild().getCategoryById("648518640718839829"))) {
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
        } else if (event.getMessage().getContentRaw().startsWith("bbn!merge") && event.getAuthor().getId().equals("477141528981012511") || event.getAuthor().getId().equals("261083609148948488")) {
            switch (event.getMessage().getContentRaw().replace("bbn!merge ", "")) {
                case "hax-dev greg-dev":
                    createPR(event, "Merge Hax's branch into Greg's branch", "hax-dev", "greg-dev");
                    break;
                case "hax-dev master":
                    createPR(event, "Merge Hax's branch into the master branch", "hax-dev", "master");
                    break;
                case "greg-dev master":
                    createPR(event, "Merge Greg's branch into the master branch", "greg-dev", "master");
                    break;
                case "greg-dev hax-dev":
                    createPR(event, "Merge Greg's branch into Hax's branch", "greg-dev", "hax-dev");
                    break;
                case "master greg-dev":
                    createPR(event, "Merge the master branch into Greg's branch", "master", "greg-dev");
                    break;
                case "master hax-dev":
                    createPR(event, "Merge the master branch into Hax's branch", "master", "hax-dev");
                    break;
            }
        }
    }

    private void createPR(GuildMessageReceivedEvent event, String s, String master, String s2) {
        try {
            GitHub connection = GitHub.connectUsingOAuth(BBNBot.config.getGitHubToken());
            GHOrganization BBN = connection.getOrganization("BigBotNetwork");
            GHRepository Hadder = BBN.getRepository("Hadder");
            GHPullRequest pr = Hadder.createPullRequest(s, master, s2, "Pull Request created by " + event.getAuthor().getAsTag());
            pr.merge("Merged!");
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Successfully created")
                    .setDescription("[Successfully created the PR on GitHub](" + pr.getHtmlUrl() + ")")
                    .setTimestamp(Instant.now())
                    .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                    .setColor(Color.GREEN)
                    .build()).queue();
        } catch (IOException e) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error while creating")
                    .setDescription("```" + e.toString() + "```")
                    .setTimestamp(Instant.now())
                    .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                    .setColor(Color.RED)
                    .build()).queue();
        }
    }
}
