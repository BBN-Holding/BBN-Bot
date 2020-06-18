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
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;

public class VoiceLogListener extends ListenerAdapter {

    Config config;

    public VoiceLogListener(Config config) {
        this.config = config;
    }

    public void sendMessage(GenericGuildVoiceEvent event) {
        TextChannel c = event.getJDA().getTextChannelById(config.getVoiceChannelID());
        event.getGuild().retrieveMember(event.getMember().getUser()).queue();

        EmbedBuilder eb = new EmbedBuilder();
        if (event instanceof GuildVoiceMuteEvent)
            eb.setTitle(event.getMember().getUser().getAsTag() + " " + ((!event.getVoiceState().isMuted()) ? "un" : "") + "muted");
        else if (event instanceof GuildVoiceDeafenEvent)
            eb.setTitle(event.getMember().getUser().getAsTag() + " " + ((!event.getVoiceState().isDeafened()) ? "un" : "") + "deafed");
        else if (event instanceof GuildVoiceJoinEvent)
            eb.setTitle(event.getMember().getUser().getAsTag() + " joined");
        else if (event instanceof GuildVoiceLeaveEvent)
            eb.setTitle(event.getMember().getUser().getAsTag() + " left");
        else if (event instanceof GuildVoiceMoveEvent) {
            eb.setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                    .setTitle(event.getMember().getUser().getAsTag() + " switched channel")
                    .addField("Old Channel", ((GuildVoiceMoveEvent) event).getChannelLeft().getName(), true)
                    .addBlankField(true)
                    .addField("Members in old channel", String.valueOf(((GuildVoiceMoveEvent) event).getChannelLeft().getMembers().size()), true);
        }

        eb.setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                .addField("Channel", event.getVoiceState().getChannel().getName(), true)
                .addField("Members in Channel", String.valueOf(event.getVoiceState().getChannel().getMembers().size()), true)
                .setColor(Color.RED)
                .setFooter("Provided by BBN", "https://bigbotnetwork.com/images/avatar.png")
                .setTimestamp(Instant.now());
        c.sendMessage(eb.build());
    }

    @Override
    public void onGuildVoiceDeafen(@Nonnull GuildVoiceDeafenEvent event) {
        this.sendMessage(event);
    }

    @Override
    public void onGuildVoiceMute(@Nonnull GuildVoiceMuteEvent event) {
        this.sendMessage(event);
    }

    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        this.sendMessage(event);
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        this.sendMessage(event);
    }

    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        this.sendMessage(event);
    }
}
