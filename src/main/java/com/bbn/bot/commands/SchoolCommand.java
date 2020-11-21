package com.bbn.bot.commands;

import com.bbn.bot.core.Config;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SchoolCommand implements Command {

    Config config;

    public SchoolCommand(Config config) {
        this.config = config;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (args.length==1) {
            if (config.getSchoolPws().containsKey(args[0])) {
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(String.valueOf(config.getSchoolPws().get(args[0]))))
                        .reason("School PW Correct").queue();
            }
        }
        event.getMessage().delete().queue();
    }

}
