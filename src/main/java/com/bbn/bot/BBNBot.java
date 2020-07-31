/*
 * Copyright 2018-2020 GregTCLTK and Schlauer-Hax
 *
 * Licensed under the MIT License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bbn.bot;

import com.bbn.bot.commands.MergeCommand;
import com.bbn.bot.commands.WarnCommand;
import com.bbn.bot.core.CommandHandler;
import com.bbn.bot.core.Config;
import com.bbn.bot.core.Sender;
import com.bbn.bot.listeners.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class BBNBot {

    public Config config = new Config("./BBN_config.json");
    public JDA jda;

    public static void main(String[] args) {
        new BBNBot().main();
    }

    public void main() {
        Sender sender = new Sender(config);
        config.load();

        CommandHandler.commands.put("warn", new WarnCommand());
        CommandHandler.commands.put("merge", new MergeCommand(config));

        JDABuilder builder = JDABuilder.createDefault(config.getToken(), GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
        builder.setActivity(Activity.streaming("on the BBN", "https://twitch.tv/bigbotnetwork"))
                .setAutoReconnect(true)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(
                        new MemberJoinListener(config),
                        new MemberLeaveListener(config),
                        new MemberBanListener(config),
                        new MessageReceiveListener(config),
                        new ReactionAddListener(config),
                        new VoiceLogListener(config),
                        new CommandListener(),
                        new StatusListener(config, sender),
                        new NewsChannelListener(config),
                        new OnlineStatusListener(config)
                );

        try {
            jda = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
