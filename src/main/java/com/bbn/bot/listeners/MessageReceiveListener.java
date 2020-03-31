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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
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
        }
    }
}
