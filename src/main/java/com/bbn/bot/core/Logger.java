package com.bbn.bot.core;

import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;

import java.io.File;

public class Logger {

    String directory;

    public Logger(String directory) {
        this.directory = directory;
        this.create();
    }

    public void create() {
        File directoryfile = new File(directory);
        if (!directoryfile.exists()) directoryfile.mkdir();
    }

    public void logStatusChange(UserUpdateOnlineStatusEvent event) {

    }

}
