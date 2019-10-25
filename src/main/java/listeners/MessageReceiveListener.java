package listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class MessageReceiveListener extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getChannel().getId().equals("449267564745588737")) {
            if (event.getMessage().getContentRaw().toLowerCase().contains("community")) {

                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("448554734312226847")).reason("Verified").queue();
                event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById("636950878615502849")).reason("Verified").queue();
                event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
                if (event.getMember().getUser().getAvatarId() == null) {
                    event.getGuild().getTextChannelsByName("log", true).get(0).sendMessage(new EmbedBuilder().setTitle("User verified").setAuthor(event.getMember().getUser().getAsTag(), "https://canary.discordapp.com/channels/@me/" + event.getMember().getId(), event.getMember().getUser().getDefaultAvatarUrl()).setTimestamp(Instant.now()).build()).queue();
                } else {
                    event.getGuild().getTextChannelsByName("log", true).get(0).sendMessage(new EmbedBuilder().setTitle("User verified").setAuthor(event.getMember().getUser().getAsTag(), "https://canary.discordapp.com/channels/@me/" + event.getMember().getId(), event.getMember().getUser().getAvatarUrl()).setTimestamp(Instant.now()).build()).queue();
                }
            } else {
                event.getMessage().delete().queueAfter(1, TimeUnit.SECONDS);
            }
        }
    }
}
