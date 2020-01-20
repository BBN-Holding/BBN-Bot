package com.bbn.bot;

import com.bbn.bot.core.Sender;
import com.bbn.bot.listeners.*;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import com.bbn.bot.util.*;

/**
* @author GregTCLTK / Skidder
 */

public class BBNBot {

    public static void main(String[] args) {

        Sender sender = new Sender();

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setActivity(Activity.streaming("on the BBN", "https://twitch.tv/bigbotnetwork"))
                .setToken(SECRETS.TOKEN)
                .setAutoReconnect(true)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .addEventListeners(new MemberJoinListener(),
                        new MessageReceiveListener(),
                        new ReactionAddListener(),
                        new MemberLeaveListener(),
                        new OnlineStatusListener(sender));

        try {
            builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
