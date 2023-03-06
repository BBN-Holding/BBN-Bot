import { ActivityType, Client, REST, Routes } from 'discord.js'
import { sendBanMessage, handleRules, sendJoinMessage, sendLeaveMessage, sendPrivateMessage, sendVoice } from './helper';
import { handleInteraction } from "./interactions";
import DB from "./sqlite";
//@ts-ignore
import * as config from './config.json'

const client = new Client({ intents: [ 3244031 ] });

const db = new DB();

client.on("ready", async () => {
    console.log(`Logged in as ${client.user!.tag}!`);
    client.user!.setActivity('bbn.one', { type: ActivityType.Listening });

    const rest = new REST({ version: '10' }).setToken(config.token);

    (async () => {
        try {
            console.log('Started refreshing application (/) commands.');

            await rest.put(Routes.applicationCommands(client.user!.id), {
                body:
                    [
                        /*{
                            name: 'setup',
                            description: 'Setup the Voice Locker',
                        },*/
                        {
                            name: 'verify',
                            description: 'Verify a User',
                        },
                        {
                            name: 'daily',
                            description: 'Claim your daily reward',
                        },
                        {
                            name: 'balance',
                            description: 'See your current balance',
                        },
                    ]
            });

            console.log('Successfully reloaded application (/) commands.');
        } catch (error) {
            console.error(error);
        }
        await db.connect();
    })();
});

client.on('guildBanAdd', (ban) => sendBanMessage(ban, true))
client.on('guildBanRemove', (ban) => sendBanMessage(ban, false))

client.on('guildMemberAdd', sendJoinMessage);
//client.on('guildMemberUpdate', handleRules)
client.on('guildMemberRemove', sendLeaveMessage)

client.on('messageCreate', (message) => sendPrivateMessage(message, client))

client.on('voiceStateUpdate', sendVoice);

client.on('interactionCreate', (interaction) => handleInteraction(interaction, db));

client.login(config.token);