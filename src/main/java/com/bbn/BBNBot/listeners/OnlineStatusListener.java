package com.bbn.BBNBot.listeners;

import com.bbn.BBNBot.core.Sender;
import com.bbn.BBNBot.util.SECRETS;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

public class OnlineStatusListener extends ListenerAdapter {

    private Sender sender;

    public OnlineStatusListener(Sender sender) {
        this.sender = sender;
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        run(event);
    }

    @Override
    public void onReconnect(@Nonnull ReconnectedEvent event) {
        run(event);
    }

    public void run(Event event) {
        new Thread(() -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sender.updateMetric(String.valueOf(event.getJDA().getGatewayPing()), String.valueOf(Instant.now().getEpochSecond()), SECRETS.DiscordGateway_metricid);
                event.getJDA().getRestPing().queue(ping ->
                        sender.updateMetric(String.valueOf(ping), String.valueOf(Instant.now().getEpochSecond()), SECRETS.DiscordRest_metricid)
                );

                for (int i = 0; i < SECRETS.BotIDs.length; i++) {
                    String id = SECRETS.BotIDs[i];
                    boolean found = false;
                    for (Guild guild : event.getJDA().getGuilds()) {
                        if (found) break;
                        for (Member member : guild.getMembers()) {
                            if (found) break;
                            if (member.getUser().getId().equals(id.split("/")[0])) {
                                boolean online = !member.getOnlineStatus().equals(OnlineStatus.OFFLINE);
                                sender.setState(id.split("/")[1], online);
                                System.out.println(id.split("/")[0]+" is online? "+online);
                                found = true;
                            }
                        }
                    }

                }


            }
        }, 1000, 300000)).start();
    }
}
