package com.bbn.bot.listeners;

import com.bbn.bot.core.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;

public class OnlineStatusListener extends ListenerAdapter {

    Config config;

    public OnlineStatusListener(Config config) {
        this.config = config;
    }

    @Override
    public void onUserUpdateOnlineStatus(@NotNull UserUpdateOnlineStatusEvent event) {
        Color color;
        switch (event.getNewOnlineStatus()) {
            case INVISIBLE:
            case UNKNOWN:
            case OFFLINE:
                color = Color.DARK_GRAY;
                break;
            case DO_NOT_DISTURB:
                color = Color.RED;
                break;
            case ONLINE:
                color = Color.GREEN;
                break;
            case IDLE:
                color = Color.YELLOW;
                break;
            default:
                color = Color.BLUE;
        }
        event.getJDA().getTextChannelById(config.getStatusLogChannelID()).sendMessage(
                new EmbedBuilder()
                        .setTitle(event.getUser().getAsTag() + " is now " + event.getNewOnlineStatus().name())
                        .setAuthor(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl(), event.getUser().getEffectiveAvatarUrl())
                        .addField("Old Status", event.getOldValue().name(), true)
                        .addField("New Status", event.getNewValue().name(), true)
                        .setFooter("Provided by BBN", "https://bigbotnetwork.com/images/avatar.png")
                        .setTimestamp(Instant.now())
                        .setColor(color).build()
        ).queue();
    }
}
