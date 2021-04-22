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

package one.bbn.bot.listeners;

import one.bbn.bot.core.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.MissingAccessException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import one.bbn.bot.core.Config;
import one.bbn.bot.core.Mongo;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.*;

public class VoiceLogListener extends ListenerAdapter {

    Config config;
    HashMap<Long, Member> events;
    Mongo mongo;

    public VoiceLogListener(Config config) {
        this.config = config;
        this.events = new HashMap<>();
        this.mongo = mongo;
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
        if (event instanceof GuildVoiceMuteEvent) {
            eb.setTitle(event.getMember().getUser().getAsTag() + " " + ((!event.getVoiceState().isMuted()) ? "un" : "") + "muted")
                    .setColor(((!event.getVoiceState().isMuted()) ? Color.GREEN : Color.RED));
        } else if (event instanceof GuildVoiceDeafenEvent)
            eb.setTitle(event.getMember().getUser().getAsTag() + " " + ((!event.getVoiceState().isDeafened()) ? "un" : "") + "deafened")
                    .setColor(((!event.getVoiceState().isDeafened()) ? Color.GREEN : Color.RED));
        else if (event instanceof GuildVoiceJoinEvent) {
            eb.setTitle(event.getMember().getUser().getAsTag() + " joined").setColor(Color.GREEN);
        } else if (event instanceof GuildVoiceLeaveEvent) {
            if (((GuildVoiceLeaveEvent) event).getChannelLeft().getMembers().size() == 0) {
                if (((GuildVoiceLeaveEvent) event).getChannelLeft().getUserLimit() != 0)
                    ((GuildVoiceLeaveEvent) event).getChannelLeft().getManager().setUserLimit(0).queue();
                if (((GuildVoiceLeaveEvent) event).getChannelLeft().getName().contains(" - Sleep"))
                    ((GuildVoiceLeaveEvent) event).getChannelLeft().getManager().setName(((GuildVoiceLeaveEvent) event).getChannelLeft()
                            .getName().replace(" - Sleep", "")).queue();
            }
            eb.setTitle(event.getMember().getUser().getAsTag() + " left")
                    .addField("Channel", ((GuildVoiceLeaveEvent) event).getChannelLeft().getName(), true)
                    .addBlankField(true)
                    .addField("Members in Channel", String.valueOf(((GuildVoiceLeaveEvent) event).getChannelLeft().getMembers().size()), true)
                    .setColor(Color.RED);
        } else if (event instanceof GuildVoiceMoveEvent) {
            if (((GuildVoiceMoveEvent) event).getChannelLeft().getMembers().size() == 0) {
                try {
                    ((GuildVoiceMoveEvent) event).getChannelLeft().getManager().setUserLimit(0).queue();
                    ((GuildVoiceMoveEvent) event).getChannelLeft().getManager().setName(((GuildVoiceMoveEvent) event).getChannelLeft()
                            .getName().replace(" - Sleep", "")).queue();
                } catch (MissingAccessException ignore) {} catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
                .addBlankField(true)
                .addField("Events in last 30 seconds", String.valueOf(count), true)
                .setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl())
                .setFooter("Provided by BBN", "https://bbn.one/images/avatar.png")
                .setTimestamp(Instant.now());

        c.sendMessage(eb.build()).queue();
        if (count == 10) {
            c.sendMessage("10 Events, kick").queue();
            event.getMember().getUser().openPrivateChannel().queue(
                    privateChannel -> privateChannel
                            .sendMessage("You got rate limited. Please rejoin in 2 minutes.")
                            .queue()
            );
            event.getGuild().kickVoiceMember(event.getMember()).queue();
            Role role = event.getGuild().getRoleById(784850371431759893L);
            event.getGuild().addRoleToMember(event.getMember(), role).queue();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
                }
            }, 120000);
        } else if (count < 10) {
            c.sendMessage(eb.build()).queue();
        }
        checkRoles(event);
    }

    public void checkRoles(GenericGuildVoiceEvent event) {
        new Thread(() -> {
            List<JSONObject> datas = mongo.getVoicestatsData(event.getGuild().getId());
            HashMap<Long, String> longtoid = new HashMap<>();
            datas.forEach(data -> {
                if (data.get("conversations") instanceof JSONArray) {
                    JSONArray array = data.getJSONArray("conversations");
                    long totalsum = 0;
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = array.getJSONObject(i);
                        if (json.has("endTime") || array.length()-1==i) {
                            long sum = (json.has("endTime") ? Long.parseLong(json.getString("endTime")) : System.currentTimeMillis()) - Long.parseLong(json.getString("startTime"));
                            sum -= getSum("idleTimes", json);
                            sum -= getSum("muteTimes", json);
                            sum -= getSum("deafTimes", json);
                            totalsum += sum;
                        }
                        System.out.println(json);
                    }
                    longtoid.put(totalsum, data.getString("userid"));
                }
            });

            // Sort and reverse the list
            Set<Map.Entry<Long, String>> set = longtoid.entrySet();
            List<Map.Entry<Long, String>> list = new ArrayList<>(set);
            list.sort((Map.Entry.comparingByKey()));
            Collections.reverse(list);

            updateRoles(event, list);
        }).start();
    }

    public void updateRoles(GenericGuildVoiceEvent event, List<Map.Entry<Long, String>> list) {
        Role role = event.getGuild().getRoleById(config.getRole());
        for (Map.Entry<Long, String> entry : list) {
            Member member = event.getGuild().getMemberById(entry.getValue());
            if (list.indexOf(entry) < 11) {
                if (!member.getPermissions().contains(Permission.ADMINISTRATOR)) {
                    if (!member.getRoles().contains(role)) {
                        event.getGuild().addRoleToMember(member, role).queue();
                    }
                }
            } else if (member.getRoles().contains(role)) {
                event.getGuild().removeRoleFromMember(member, role).queue();
            }
        }
    }

    public long getSum(String name, JSONObject json) {
        if (json.has(name)) {
            JSONArray data = json.getJSONArray(name);
            String endtime = json.has("endTime") ? json.getString("endTime") : String.valueOf(System.currentTimeMillis());
            long sum = 0;
            if (data == null) return 0;
            for (Object datobj : data) {
                String dat = (String) datobj;
                if (dat.endsWith("-")) dat += endtime;
                sum += Long.parseLong(dat.split("-")[1]) - Long.parseLong(dat.split("-")[0]);
            }
            return sum;
        }
        return 0;
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
