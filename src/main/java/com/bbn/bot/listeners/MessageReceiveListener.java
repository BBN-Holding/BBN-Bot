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

import com.bbn.bot.core.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class MessageReceiveListener extends ListenerAdapter {

    Config config;

    public MessageReceiveListener(Config config) {
        this.config = config;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getChannel().getId().equals("757990314937155646")) {
            if (e.getMessage().getContentRaw().toLowerCase().contains("community")) {
                e.getGuild().addRoleToMember(e.getMember(), e.getGuild().getRoleById(config.getCommunityRoleID())).reason("Verified").queue();
                e.getGuild().removeRoleFromMember(e.getMember(), e.getGuild().getRoleById(config.getUnVerifiedRoleID())).reason("Verified").queue();
                e.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
                e.getGuild().getTextChannelById(config.getLogChannelID()).sendMessage(new EmbedBuilder()
                        .setTitle("User verified")
                        .setAuthor(e.getMember().getUser().getAsTag(), e.getMember().getUser().getEffectiveAvatarUrl(), e.getMember().getUser().getEffectiveAvatarUrl())
                        .addField("User Creation Time", e.getMember().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                        .addField("ID", e.getMember().getId(), true)
                        .setTimestamp(Instant.now())
                        .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                        .setColor(Color.GREEN)
                        .build()).queue();
            } else {
                e.getMessage().delete().queueAfter(1, TimeUnit.SECONDS);
            }
        }
    }

    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent e) {
        e.getJDA().getTextChannelById(config.getLogChannelID()).sendMessage(new EmbedBuilder()
                .setTitle("Private message received")
                .setDescription("```" + e.getMessage().getContentRaw() + "```")
                .setAuthor(e.getAuthor().getAsTag(), null, e.getAuthor().getAvatarUrl())
                .addField("Mention", e.getAuthor().getAsMention(), true)
                .addField("User ID", e.getAuthor().getId(), true)
                .addBlankField(true)
                .addField("User Creation Time", e.getAuthor().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                .addField("Message ID", e.getMessageId(), true)
                .setTimestamp(Instant.now())
                .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                .setColor(Color.GREEN)
                .build()).queue();
        super.onPrivateMessageReceived(e);
    }
}
