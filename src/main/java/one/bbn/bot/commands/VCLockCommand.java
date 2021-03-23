/*
 * Copyright 2018-2021 GregTCLTK and Schlauer-Hax
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

package one.bbn.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class VCLockCommand implements Command {
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("Voicelocker")
                    .setDescription(event.getGuild().getEmoteById("780057996712476703").getAsMention() + " - Netflix Mode\n\uD83C\uDF1B - Sleep Mode\n\uD83D\uDED1 - Reset").build()).queue(
                    msg -> {
                        msg.addReaction(event.getGuild().getEmoteById("780057996712476703")).queue();
                        msg.addReaction("\uD83C\uDF1B").queue();
                        msg.addReaction("\uD83D\uDED1").queue();
                    }
            );
        }
    }
}
