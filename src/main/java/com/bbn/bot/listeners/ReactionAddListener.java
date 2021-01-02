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
            if (event.getReactionEmote().getEmoji().equals("✅")) {
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
            } else {
                event.getReaction().removeReaction(event.getUser()).queue();
            }

        }
        if (!event.getUser().isBot() && event.getChannelType() == ChannelType.TEXT) {
            Guild guild = event.getJDA().getGuildChannelById(config.getLogChannelID()).getGuild();
            if (event.getTextChannel().getId().equals("788844358592888863")) {
                Member member = guild.getMember(event.getUser());
                if (member.getVoiceState().inVoiceChannel()) {
                    VoiceChannel vc = member.getVoiceState().getChannel();
                    switch (event.getReactionEmote().getName()) {
                        case "Netflix":
                            vc.getManager().setUserLimit(vc.getMembers().size()).queue();
                            break;
                        case "\uD83D\uDED1":
                            vc.getManager().setUserLimit(0).queue();
                            vc.getManager().setName(vc.getName().replace(" - Sleep", "")).queue();
                            break;
                        case "\uD83C\uDF1B":
                            vc.getManager().setName((vc.getName().endsWith(" - Sleep")) ?
                                    vc.getName().replace(" - Sleep", "") :
                                    vc.getName()+" - Sleep").queue();
                            break;
                    }
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
            }
        }
    }
}
