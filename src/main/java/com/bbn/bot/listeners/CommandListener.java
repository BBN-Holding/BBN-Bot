package com.bbn.bot.listeners;

import com.bbn.bot.core.CommandHandler;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().startsWith("bbn!") && !event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId()) && event.getChannelType().equals(ChannelType.TEXT)) {
            CommandHandler.handleCommand(CommandHandler.parser.parse(event.getMessage().getContentRaw(), event));
        }
        super.onMessageReceived(event);
    }
}
