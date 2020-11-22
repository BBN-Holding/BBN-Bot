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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.time.LocalTime;

public class VoiceLogListener extends ListenerAdapter {

    Config config;
    HashMap<Long, Member> events;

    public VoiceLogListener(Config config) {
        this.config = config;
        this.events = new HashMap<>();
    }

    public void sendMessage(GenericGuildVoiceEvent event) {

        HashMap<Long, Member> temp = new HashMap<>();

        TextChannel c = event.getJDA().getTextChannelById(config.getVoiceChannelID());
        event.getGuild().retrieveMember(event.getMember().getUser()).queue();

        events.put(System.currentTimeMillis(), event.getMember());

        events.forEach((timestamp, member) -> {
                    if (timestamp + 30000 < System.currentTimeMillis()) {
                        temp.put(timestamp, member);
                    }
                }
        );

        temp.forEach((timestamp, member) -> events.remove(timestamp, member));

        int count = 0;
        for (Map.Entry<Long, Member> entry : events.entrySet()) {
            if (entry.getValue().getIdLong() == event.getMember().getIdLong()) count++;
        }

        EmbedBuilder eb = new EmbedBuilder();
        if (event instanceof GuildVoiceMuteEvent)
            eb.setTitle(event.getMember().getUser().getAsTag() + " " + ((!event.getVoiceState().isMuted()) ? "un" : "") + "muted")
                    .setColor(((!event.getVoiceState().isMuted()) ? Color.GREEN : Color.RED));
        else if (event instanceof GuildVoiceDeafenEvent)
            eb.setTitle(event.getMember().getUser().getAsTag() + " " + ((!event.getVoiceState().isDeafened()) ? "un" : "") + "deafened")
                    .setColor(((!event.getVoiceState().isDeafened()) ? Color.GREEN : Color.RED));
        else if (event instanceof GuildVoiceJoinEvent) {
            if (((GuildVoiceJoinEvent) event).getChannelJoined().getParent().getName().equalsIgnoreCase("Voice Channels"))
                if (event.getMember().getUser().getId().equals("261083609148948488") || event.getMember().getUser().getId().equals("401817301919465482"))
                    event.getMember().getUser().openPrivateChannel().queue(
                            channel -> channel.sendMessage(new EmbedBuilder()
                                    .setTitle("Voice Locker")
                                    .setDescription("Hey Gamer, hier kannst du auswählen was ihr macht und ich stelle den " +
                                            "Channel richtig ein\n" +
                                            "<:AmongUs:780057870573109258> - Member limit=10\n" +
                                            "<:Netflix:780057996712476703> - Member limit=Leute die drin sind\n" +
                                            "\uD83D\uDED1 - reset time").build()
                            ).queue(
                                    msg -> {
                                        msg.addReaction(event.getGuild().getEmoteById("780057996712476703")).queue();
                                        msg.addReaction(event.getGuild().getEmoteById("780057870573109258")).queue();
                                        msg.addReaction("\uD83D\uDED1").queue();
                                    }
                            )
                    );
            eb.setTitle(event.getMember().getUser().getAsTag() + " joined").setColor(Color.GREEN);
            if (((GuildVoiceJoinEvent) event).getChannelJoined().getMembers().size() == 1)
                startKickTimeout(event.getMember());
        } else if (event instanceof GuildVoiceLeaveEvent) {
            if (((GuildVoiceLeaveEvent) event).getChannelLeft().getMembers().size() == 1) {
                startKickTimeout(((GuildVoiceLeaveEvent) event).getChannelLeft().getMembers().get(0));
            } else if (((GuildVoiceLeaveEvent) event).getChannelLeft().getMembers().size() == 0)
                ((GuildVoiceLeaveEvent) event).getChannelLeft().getManager().setUserLimit(0).queue();
            eb.setTitle(event.getMember().getUser().getAsTag() + " left")
                    .addField("Channel", ((GuildVoiceLeaveEvent) event).getChannelLeft().getName(), true)
                    .addBlankField(true)
                    .addField("Members in Channel", String.valueOf(((GuildVoiceLeaveEvent) event).getChannelLeft().getMembers().size()), true)
                    .setColor(Color.RED);
        } else if (event instanceof GuildVoiceMoveEvent) {
            if (((GuildVoiceMoveEvent) event).getChannelLeft().getMembers().size() == 1)
                startKickTimeout(((GuildVoiceMoveEvent) event).getChannelLeft().getMembers().get(0));
            if (((GuildVoiceMoveEvent) event).getChannelJoined().getMembers().size() == 1)
                startKickTimeout(((GuildVoiceMoveEvent) event).getChannelJoined().getMembers().get(0));
            eb.setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                    .setTitle(event.getMember().getUser().getAsTag() + " switched channel")
                    .addField("Old Channel", ((GuildVoiceMoveEvent) event).getChannelLeft().getName(), true)
                    .addBlankField(true)
                    .addField("Members in old channel", String.valueOf(((GuildVoiceMoveEvent) event).getChannelLeft().getMembers().size()), true)
                    .setColor(Color.ORANGE);
        }

        if (event.getVoiceState().getChannel() != null)
            eb.addField("Channel", event.getVoiceState().getChannel().getName(), true)
                    .addBlankField(true)
                    .addField("Members in Channel", String.valueOf(event.getVoiceState().getChannel().getMembers().size()), true);

        eb.addField("Current Time", LocalTime.now().toString(), true)
                .addField("Events in last 30 seconds", String.valueOf(count), true)
                .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                .setFooter("Provided by BBN", "https://bigbotnetwork.com/images/avatar.png")
                .setTimestamp(Instant.now());

        c.sendMessage(eb.build()).queue();
        if (count > 10) {
            c.sendMessage("Over 10 Events, kick").queue();
            event.getMember().getUser().openPrivateChannel().queue(
                    privateChannel -> privateChannel
                            .sendMessage("You got rate limited. Please rejoin in 30 seconds. https://discord.gg/nPwjaJk")
                            .queue(ignore -> event.getMember().kick("Voice Log Spam").queue())
            );
        }
    }

    HashMap<String, Long> timeoutList = new HashMap<>();

    public void startKickTimeout(Member member) {
        long startTime = System.currentTimeMillis();
        timeoutList.remove(member.getId());
        timeoutList.put(member.getId(), startTime);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (timeoutList.get(member.getId()) == startTime) {
                    if (member.getVoiceState().getChannel() != null) {
                        if (member.getVoiceState().getChannel().getMembers().size() == 1) {
                            member.getGuild().kickVoiceMember(member).queue();
                        }
                    }
                }
            }
        }, 600000);
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
