import { ActivityType, Client, Message, MessageType, REST, Routes } from 'discord.js'
import { sendBanMessage, sendJoinMessage, sendLeaveMessage, sendPrivateMessage, sendVoice } from './helper';
import { handleInteraction } from "./interactions";
import DB from "./db";
//@ts-ignore
import * as config from './config.json'

const client = new Client({ intents: [3244031] });

const db = new DB(config.dburl);

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
                        // {
                        //     name: 'setup',
                        //     description: 'Setup the Voice Locker',
                        // },
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
                            options: [
                                {
                                    name: 'user',
                                    description: 'Check the balance of another user',
                                    type: 9,
                                    required: false,
                                }
                            ]
                        },
                        {
                            name: 'addcoins',
                            description: 'Add coins to a user',
                            options: [
                                {
                                    name: 'user',
                                    description: 'The user to add coins to',
                                    type: 9,
                                    required: true,
                                },
                                {
                                    name: 'coins',
                                    description: 'The amount of coins to add',
                                    type: 4,
                                    required: true,
                                }
                            ]
                        },
                        {
                            name: 'removecoins',
                            description: 'Remove coins from a user',
                            options: [
                                {
                                    name: 'user',
                                    description: 'The user to remove coins from',
                                    type: 9,
                                    required: true,
                                },
                                {
                                    name: 'coins',
                                    description: 'The amount of coins to remove',
                                    type: 4,
                                    required: true,
                                }
                            ]
                        },
                        {
                            name: 'escalate',
                            description: 'Escalate a ticket to the next support level'
                        }
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
client.on('guildMemberRemove', sendLeaveMessage)

client.on('messageCreate', (message) => sendPrivateMessage(message, client))

client.on('voiceStateUpdate', sendVoice);

client.on('interactionCreate', (interaction) => handleInteraction(interaction, db));
client.on('messageCreate', async (message) => {
    if (message.type === MessageType.GuildBoost && message.channelId === config.log_channel) {
        const dbuser = await db.finduser(message.author.id);
        if (!dbuser) {
            const channel = await message.guild?.channels.create({
                name: 'link-'+message.author.id,
                parent: config.link_category,
                permissionOverwrites: [
                    {
                        id: message.author.id,
                        allow: ['ViewChannel']
                    }
                ]
            });
            await channel?.send({ content: `<@${message.author.id}> Looks like you just boosted the server! Unfortunately, you are not linked to your BBN account yet. Please send your Email address to this channel to link your account. Our Team will respond as soon as possible.`, allowedMentions: { users: [message.author.id] }});
        } else {
            await db.addBoosterRewards(message.author.id);
            await message.channel.send(`Added booster rewards for https://discord.com/channels/${message.guildId}/${message.channelId}/${message.id}`);
        }
    }
})

async function checkBoosts() {
    console.log('Checking boosts');
    const guild = await client.guilds.fetch(config.guild_id);
    const log_channel = await guild.channels.fetch(config.log_channel);
    if (!log_channel?.isTextBased()) return;
    // Get all messages in the last 32 days
    const messages: Message[] = [];
    let scan = true;
    let last_id = await log_channel.messages.fetch({ limit: 1 }).then((msgs) => msgs.first()!.id);
    while (scan) {
        const msgs = await log_channel.messages.fetch({ limit: 100, before: last_id });
        last_id = msgs.last()!.id;
        if (msgs.size === 0 || msgs.last()!.createdAt.getTime() < Date.now() - 33 * 24 * 60 * 60 * 1000) {
            scan = false;
        } else {
            msgs.forEach((msg) => {
                if (msg.type === MessageType.GuildBoost && msgs.last()!.createdAt.getTime() < Date.now() - 30 * 24 * 60 * 60 * 1000) {
                    messages.push(msg);
                }
            })
        }
    }
    messages.forEach(async (msg) => {
        const user = await db.finduser(msg.author.id);
        if (user) {
            await db.removeBoosterRewards(msg.author.id);
            await msg.channel.send(`Removed booster rewards for https://discord.com/channels/${guild.id}/${msg.channelId}/${msg.id}`);
        } else {
            await msg.channel.send({ content: 'User not found, <@757969277063266407> please check this user https://discord.com/channels/${guild.id}/${msg.channelId}/${msg.id}', allowedMentions: { roles: ['757969277063266407'] } });
        }
    });
}

setInterval(checkBoosts, 24 * 60 * 60 * 1000);

client.login(config.token).then(() => {
    console.log('Logged in'); 
    checkBoosts();
});