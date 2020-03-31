package com.bbn.bot.core;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;

public class CommandParser {

    public commandContainer parse(String raw, MessageReceivedEvent event) {
        String beheaded = raw.replaceFirst("\\?", "");
        String[] splitBeheaded = beheaded.split(" ");
        String invoke = splitBeheaded[0];
        ArrayList<String> split = new ArrayList<>();
        Collections.addAll(split, splitBeheaded);
        String[] args = new String[split.size() - 1];
        split.subList(1, split.size()).toArray(args);

        return new commandContainer(raw, beheaded, splitBeheaded, invoke, args, event);
    }


    static class commandContainer {

        final String raw;
        final String beheaded;
        final String[] splitBeheaded;
        final String invoke;
        final String[] args;
        final MessageReceivedEvent event;

        commandContainer(String rw, String beheaded, String[] splitBeheaded, String invoke, String[] args, MessageReceivedEvent event) {
            this.raw = rw;
            this.beheaded = beheaded;
            this.splitBeheaded = splitBeheaded;
            this.invoke = invoke;
            this.args = args;
            this.event = event;
        }

    }

}
