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
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class ReactionAddListener extends ListenerAdapter {

    Config config;

    public ReactionAddListener(Config config) {
        this.config = config;
    }

    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getMessageId().equals(config.getVerifyMessageID())) {
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(config.getCommunityRoleID())).reason("Verified").queue();
            event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(config.getUnVerifiedRoleID())).reason("Verified").queue();
            event.getReaction().removeReaction(event.getUser()).queue();
            event.getGuild().getTextChannelById(config.getLogChannelID()).sendMessage(new EmbedBuilder()
                    .setTitle("User verified")
                    .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getEffectiveAvatarUrl(), event.getMember().getUser().getEffectiveAvatarUrl())
                    .addField("User Creation Time", event.getMember().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                    .addField("ID", event.getMember().getId(), true)
                    .setTimestamp(Instant.now())
                    .setFooter("BigBotNetwork", "https://bigbotnetwork.com/images/avatar.png")
                    .setColor(Color.GREEN)
                    .build()).queue();

        }
        if (!event.getUser().isBot()) {
            if (event.getChannelType() == ChannelType.PRIVATE) {
                Guild guild = event.getJDA().getGuildChannelById(config.getLogChannelID()).getGuild();
                Member member = guild.getMember(event.getUser());
                VoiceChannel vc = member.getVoiceState().getChannel();
                switch (event.getReactionEmote().getName()) {
                    case "Netflix":
                        vc.getManager().setUserLimit(vc.getMembers().size()).queue();
                        break;
                    case "AmongUs":
                        vc.getManager().setUserLimit(10).queue();
                        break;
                    case "\uD83D\uDED1":
                        vc.getManager().setUserLimit(0).queue();
                        break;
                }
                event.getPrivateChannel().deleteMessageById(event.getMessageId()).queue(
                        bruh -> event.getPrivateChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Voice Locker")
                                .setDescription("Hey Gamer, hier kannst du auswählen was ihr macht und ich stelle den " +
                                        "Channel richtig ein\n" +
                                        "<:AmongUs:780057870573109258> - Member limit=10\n" +
                                        "<:Netflix:780057996712476703> - Member limit=Leute die drin sind\n" +
                                        "\uD83D\uDED1 - reset time").build()
                        ).queue(
                                msg -> {
                                    msg.addReaction(guild.getEmoteById("780057996712476703")).queue();
                                    msg.addReaction(guild.getEmoteById("780057870573109258")).queue();
                                    msg.addReaction("\uD83D\uDED1").queue();
                                }
                        )
                );
            }
        }
    }
}
