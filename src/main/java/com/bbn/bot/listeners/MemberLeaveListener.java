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

package com.bbn.bot.listeners;

import com.bbn.bot.core.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

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
        super.onGuildMemberRemove(event);
    }
}
