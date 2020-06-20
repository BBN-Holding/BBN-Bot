package com.bbn.bot.listeners;

import com.bbn.bot.core.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class NewsChannelListener extends ListenerAdapter {

    Config config;

    public NewsChannelListener(Config config) {
        this.config = config;
    }

    ArrayList<Message> lastmessages = new ArrayList<>();

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        this.updateNewsChannel(event.getJDA());
    }

    @Override
    public void onUserUpdateOnlineStatus(@Nonnull UserUpdateOnlineStatusEvent event) {
        this.updateNewsChannel(event.getJDA());
    }

    @Override
    public void onUserActivityStart(@Nonnull UserActivityStartEvent event) {
        this.updateNewsChannel(event.getJDA());
    }

    @Override
    public void onUserActivityEnd(@Nonnull UserActivityEndEvent event) {
        this.updateNewsChannel(event.getJDA());
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        boolean isvclog = event.getChannel().getId().equals(config.getVoiceChannelID()) && event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId());
        boolean isnews = event.getChannel().getId().equals(config.getNewsChannelID());
        if (!isvclog && !isnews) {
            if (lastmessages.size() == 5) {
                lastmessages.remove(4);
            }
            lastmessages.add(0, event.getMessage());
        }
        this.updateNewsChannel(event.getJDA());
    }

    public void updateNewsChannel(JDA jda) {
        TextChannel channel = jda.getTextChannelById(config.getNewsChannelID());
        MessageHistory history = channel.getHistory();
        history.retrievePast(100).queue(
                success -> {
                    if (history.size() != 0) {
                        for (int i = 0; i < history.size(); i++) {
                            Message message = history.getRetrievedHistory().get(i);
                            if (message.getAuthor().getId().equals(jda.getSelfUser().getId())) {
                                if (i != 0) {
                                    String lastlastmessages = message.getEmbeds().get(0).getFields().get(1).getValue();
                                    message.delete().queue();
                                    channel.sendMessage(new EmbedBuilder().setTitle("This Message will get updated").build()).queue(msgtoupdate -> this.updateMessage(msgtoupdate, lastlastmessages));
                                    return;
                                } else {
                                    this.updateMessage(message, null);
                                    return;
                                }
                            }
                        }
                        channel.sendMessage(new EmbedBuilder().setTitle("This Message will get updated").build()).queue(msgtoupdate -> this.updateMessage(msgtoupdate, null));
                    } else {
                        channel.sendMessage(new EmbedBuilder().setTitle("This Message will get updated").build()).queue(msgtoupdate -> this.updateMessage(msgtoupdate, null));
                    }
                }
        );
    }

    public void updateMessage(Message message, String lastlastmessages) {
        ArrayList<String> vcactions = new ArrayList<>();
        TextChannel voicelogchannel = message.getGuild().getTextChannelById(config.getVoiceChannelID());
        assert voicelogchannel != null;
        voicelogchannel.getHistory().retrievePast(100).queue(
                messages -> {
                    for (int i = 0; vcactions.size() < 5; i++) {
                        Message vcmessage = messages.get(i);
                        if (vcmessage.getAuthor().getId().equals(message.getJDA().getSelfUser().getId())) {
                            if (vcmessage.getEmbeds().size() == 1) {
                                MessageEmbed messageEmbed = vcmessage.getEmbeds().get(0);
                                vcactions.add(String.format("%s - %s (%s)", messageEmbed.getTitle(),
                                        messageEmbed.getFields().get(0).getValue(), messageEmbed.getFields().get(1).getValue()));
                            }
                        }
                    }
                    String lastmessagesoutput;
                    if (lastlastmessages == null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Message lastmessage : lastmessages) {
                            String attachment = "";
                            if (lastmessage.getEmbeds().size() > 0)
                                attachment += String.format(",%d Embeds", lastmessage.getEmbeds().size());
                            if (lastmessage.getAttachments().size() > 0)
                                attachment += String.format(",%d Attachments", lastmessage.getAttachments().size());
                            if (attachment.length() != 0)
                                attachment = String.format("(%s)", attachment.replaceFirst(",", ""));
                            stringBuilder.append(String.format("#%s - %s: %s %s\n", lastmessage.getChannel().getName(),
                                    lastmessage.getAuthor().getAsTag(), lastmessage.getContentRaw(), attachment));
                        }
                        if (stringBuilder.chars().count() == 0) {
                            lastmessagesoutput = "No new captured Messages";
                        } else lastmessagesoutput = stringBuilder.toString();
                    } else lastmessagesoutput = lastlastmessages;

                    Role insiderrole = message.getGuild().getRoleById(config.getInsiderRoleID());
                    message.getGuild().retrieveMembers();
                    ArrayList<String> statusinsider = new ArrayList<>();
                    List<Member> insider = message.getGuild().getMembersWithRoles(insiderrole);
                    for (int i = 0; i < insider.size(); i++) {
                        Member member = insider.get(i);
                        if (member.getOnlineStatus() != OnlineStatus.OFFLINE) {
                            String game = "";
                            if (member.getActivities().size() != 0) {
                                for (Activity activity : member.getActivities()) {
                                    if (!activity.getName().equals("Custom Status"))
                                        game += String.format(", %s", activity.getName());
                                }
                            }
                            game = game.replaceFirst(", ", "");
                            String vc = (member.getVoiceState() != null) ? ((member.getVoiceState().getChannel() != null) ?
                                    "(" + member.getVoiceState().getChannel().getName() + ")" : "") : "";
                            if (game.length() != 0) game = String.format(" - %s", game);
                            statusinsider.add(String.format("%s - %s %s%s", member.getUser().getAsTag(), member.getOnlineStatus(), vc, game));
                        }
                    }

                    message.editMessage(
                            new EmbedBuilder()
                                    .setTitle("Overview")
                                    .addField("Last 5 Actions in #voicelog", String.join("\n", vcactions.toArray(new String[0])), false)
                                    .addField("Last 5 new Messages", lastmessagesoutput, false)
                                    .addField("Online Insiders", String.join("\n", statusinsider.toArray(new String[0])), false)
                                    .setFooter("Last updated", "https://bigbotnetwork.com/images/avatar.png")
                                    .setTimestamp(Instant.now())
                                    .build()
                    ).queue();
                }
        );
    }
}
