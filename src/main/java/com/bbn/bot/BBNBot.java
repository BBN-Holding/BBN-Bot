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

import com.bbn.bot.commands.CloseCommand;
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

import java.nio.file.Files;
import java.nio.file.Paths;

public class BBNBot {

    public static Config config = new Config("./BBN_config.json");
    public static JDA jda;

    public static void main(String[] args) {

        config.load();

        CommandHandler.commands.put("warn", new WarnCommand());
        CommandHandler.commands.put("close", new CloseCommand());
        CommandHandler.commands.put("merge", new MergeCommand());

        JDABuilder builder = JDABuilder.createDefault(config.getToken());
        builder.setActivity(Activity.streaming("on the BBN", "https://twitch.tv/bigbotnetwork"))
                .setAutoReconnect(true)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .addEventListeners(new MemberJoinListener(),
                        new MessageReceiveListener(),
                        new ReactionAddListener(),
                        new MemberLeaveListener(),
                        new VoiceLogListener(),
                        new CommandListener());

        if (Files.notExists(Paths.get("./pom.xml"))) {
            Sender sender = new Sender();
            builder.addEventListeners(new OnlineStatusListener(sender));
        }

        try {
            jda = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
