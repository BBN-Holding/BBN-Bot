package com.bbn.bot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Command {
    void action(String[] args, MessageReceivedEvent event);
}
