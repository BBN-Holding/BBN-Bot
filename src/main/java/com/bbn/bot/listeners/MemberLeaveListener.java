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
import com.bbn.bot.core.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.kohsuke.github.GHContentUpdateResponse;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MemberLeaveListener extends ListenerAdapter {

    Config config;

    public MemberLeaveListener(Config config) {
        this.config = config;
    }

    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        event.getGuild().getTextChannelById(config.getLogChannelID()).sendMessage(new EmbedBuilder()
                .setTitle(((!event.getMember().getUser().isBot()) ? "User" : "Bot") + " left")
                .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getEffectiveAvatarUrl(), event.getMember().getUser().getEffectiveAvatarUrl())
                .addField(((!event.getMember().getUser().isBot()) ? "User" : "Bot") + " Creation Time", event.getMember().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                .addField("ID", event.getMember().getId(), true)
                .setTimestamp(Instant.now())
                .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                .setColor(Color.RED)
                .build()).queue();

        if (event.getGuild().getTextChannelsByName(event.getUser().getName(), true).size() > 0) {
            TextChannel channel = event.getGuild().getTextChannelsByName(event.getUser().getName(), true).get(0);
            if (channel.getTopic().contains(event.getUser().getId())) {
                channel.getManager().setParent(event.getGuild().getCategoryById(config.getArchiveCategoryID()))
                        .reason("Case closed").queue();
                channel.getManager().setName(channel.getName() + "-archive").queue();
                try {
                    GitHub connection = GitHub.connectUsingOAuth(config.getGitHubToken());
                    GHRepository Mining = connection.getMyself().getRepository("Data-Mining");
                    String pattern = "dd-MM-yyyy";
                    String date = new SimpleDateFormat(pattern).format(new Date());
                    GHContentUpdateResponse commit = Mining.createContent().branch("master")
                            .path(date + "/" + channel.getId() + ".md")
                            .content(channel.getHistory().retrievePast(100).complete().toString())
                            .message("Channel by " + channel.getName() + " archived")
                            .commit();
                    commit.getCommit().createComment("Archived by MemberLeaveListener", channel.getId() + ".md", 1, 1);
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
                            .setDescription("```" + e.toString() + "```")
                            .setTimestamp(Instant.now())
                            .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                            .setColor(Color.RED)
                            .build()).queue();
                    e.printStackTrace();
                }
            }
        }
        super.onGuildMemberRemove(event);
    }
}
