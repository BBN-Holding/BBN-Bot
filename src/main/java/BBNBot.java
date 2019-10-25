import listeners.*;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import util.SECRETS;

import javax.security.auth.login.LoginException;

public class BBNBot {
    public static JDABuilder builder;

    public static void main(String[] args) {

        try {

            builder = new JDABuilder(AccountType.BOT).setToken(SECRETS.TOKEN).setAutoReconnect(true).setStatus(OnlineStatus.ONLINE);
            builder.setActivity(Activity.streaming("on the BBN", "https://twitch.tv/bigbotnetwork"));
            builder.addEventListeners(new MemberJoinListener(), new MessageReceiveListener(), new ReactionAddListener());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
