/*
 * Copyright 2018-2021 GregTCLTK and Schlauer-Hax
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

package com.bbn.bot.commands;

import com.bbn.bot.core.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.kohsuke.github.*;

import java.awt.*;
import java.time.Instant;

public class MergeCommand implements Command {

    Config config;

    public MergeCommand(Config config) {
        this.config = config;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (event.getAuthor().getId().equals("477141528981012511") || event.getAuthor().getId().equals("261083609148948488")) {
            if (args.length == 2) {
                try {
                    GitHub connection = GitHub.connectUsingOAuth(config.getGitHubToken());
                    GHOrganization BBN = connection.getOrganization("BigBotNetwork");
                    GHRepository Hadder = BBN.getRepository("Hadder");
                    GHPullRequest pr = Hadder.createPullRequest("Merge branches by BBNBot", args[0], args[1], "Pull Request created by " + event.getAuthor().getAsTag());
                    pr.merge("Merged!");
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Successfully created")
                            .setDescription("[Successfully created the PR on GitHub](" + pr.getHtmlUrl() + ")")
                            .setTimestamp(Instant.now())
                            .setFooter("BBN", "https://bbn.one/images/avatar.png")
                            .setColor(Color.GREEN)
                            .build()).queue();
                } catch (HttpException e) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Invalid branch")
                            .setDescription("One or both branch names are invalid.")
                            .setTimestamp(Instant.now())
                            .setFooter("BBN", "https://bbn.one/images/avatar.png")
                            .setColor(Color.RED)
                            .build()).queue();
                } catch (Exception e) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error while creating")
                            .setDescription("```" + e.toString() + "```")
                            .setTimestamp(Instant.now())
                            .setFooter("BBN", "https://bbn.one/images/avatar.png")
                            .setColor(Color.RED)
                            .build()).queue();
                }
            } else {
                event.getTextChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Missing branch(es)")
                        .setDescription("You have to specify two different branch names. Separated by a space.")
                        .setTimestamp(Instant.now())
                        .setFooter("BBN", "https://bbn.one/images/avatar.png")
                        .setColor(Color.RED)
                        .build()).queue();
            }
        } else {
            event.getTextChannel().sendMessage(new EmbedBuilder()
                    .setTitle("No permission")
                    .setDescription("You aren't allowed to use this command.")
                    .setTimestamp(Instant.now())
                    .setFooter("BBN", "https://bbn.one/images/avatar.png")
                    .setColor(Color.RED)
                    .build()).queue();
        }
    }
}
