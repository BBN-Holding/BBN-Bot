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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class MemberBanListener extends ListenerAdapter {

    Config config;

    public MemberBanListener(Config config) {
        this.config = config;
    }

    @Override
    public void onGuildBan(@Nonnull GuildBanEvent event) {
        String reason = event.getGuild().retrieveBanById(event.getUser().getId()).complete().getReason();
        if (reason == null) reason = "Not specified";
        this.sendMessage(reason, event.getUser(), event.getGuild());
    }

    @Override
    public void onGuildUnban(@Nonnull GuildUnbanEvent event) {
        this.sendMessage(null, event.getUser(), event.getGuild());
    }

    public void sendMessage(String reason, User user, Guild guild) {
        guild.getTextChannelById(config.getLogChannelID()).sendMessage(new EmbedBuilder()
                .setTitle(((user.isBot()) ? "Bot" : "User") + ((reason==null) ? " unbanned" : " banned"))
                .setAuthor(user.getAsTag(), user.getEffectiveAvatarUrl(), user.getEffectiveAvatarUrl())
                .addField(((user.isBot()) ? "Bot" : "User") + " Creation Time", user.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                .addField("ID", user.getId(), true)
                .addField("Reason", reason==null ? "Not specified" : reason, false)
                .setTimestamp(Instant.now())
                .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                .setColor(Color.RED)
                .build()).queue();
    }
}
