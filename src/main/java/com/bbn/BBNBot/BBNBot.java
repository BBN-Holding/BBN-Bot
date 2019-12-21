package com.bbn.BBNBot;

import com.bbn.BBNBot.core.Sender;
import com.bbn.BBNBot.listeners.*;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import com.bbn.BBNBot.util.*;

/**
* @author GregTCLTK / Skidder
 */

public class BBNBot {
    private static JDABuilder builder;

    public static void main(String[] args) {

        Sender sender = new Sender();

        try {
            builder = new JDABuilder(AccountType.BOT).setToken(SECRETS.TOKEN).setAutoReconnect(true).setStatus(OnlineStatus.DO_NOT_DISTURB);
            builder.setActivity(Activity.streaming("on the BBN", "https://twitch.tv/bigbotnetwork"));
            builder.addEventListeners(new MemberJoinListener(),
                    new MessageReceiveListener(),
                    new ReactionAddListener(),
                    new MemberLeaveListener(),
                    new OnlineStatusListener(sender));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
