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

package com.bbn.bot.listeners;

import com.bbn.bot.core.Config;
import com.bbn.bot.core.Sender;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StatusListener extends ListenerAdapter {

    Config config;

    public StatusListener(Config config, Sender sender) {
        this.config = config;
        this.sender = sender;
    }

    private Sender sender;
    private ArrayList<String> BotIDs = new ArrayList<>();

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        run(event);
    }

    @Override
    public void onReconnect(@Nonnull ReconnectedEvent event) {
        run(event);
    }

    public void run(Event event) {
        new Thread(() -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sender.updateMetric(String.valueOf(event.getJDA().getGatewayPing()), String.valueOf(Instant.now().getEpochSecond()), config.getDCGID());
                event.getJDA().getRestPing().queue(ping ->
                        sender.updateMetric(String.valueOf(ping), String.valueOf(Instant.now().getEpochSecond()), config.getDCRID())
                );

                int length = config.getBotIDs().length();
                for (int i = 0; i < length; i++) {
                    BotIDs.add(config.getBotIDs().get(i).toString());
                }

                for (String id : BotIDs) {
                    Guild g = event.getJDA().getGuildById("757966278936756345");
                    g.retrieveMemberById(id.split("/")[0]).queue((member) -> {
                        boolean online = !member.getOnlineStatus().equals(OnlineStatus.OFFLINE);
                        sender.setState(id.split("/")[1], online);
                    });
                }
            }
        }, 1000, 100000)).start();
    }
}
