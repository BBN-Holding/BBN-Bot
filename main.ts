import { ActivityType, CategoryChannel, Client, Message, MessageType, REST, Routes } from 'npm:discord.js'
import { handleShowcaseMessage, sendBanMessage, sendLeaveMessage, sendPrivateMessage, sendVoice } from './helper.ts';
import { handleInteraction } from "./interactions.ts";
import { PartnerManager } from './partner.ts';
import { addBoosterRewards, finduser, removeBoosterRewards } from "./db.ts";

const client = new Client({ intents: 3276799 });

client.on("ready", () => {
    console.log(`Logged in as ${client.user!.tag}!`);
    client.user!.setActivity('bbn.one', { type: ActivityType.Listening });

    const rest = new REST().setToken(Deno.env.get("TOKEN")!);

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
                        },
                        {
                            name: 'addpartner',
                            description: 'Add a partner to the partner list',
                            options: [
                                {
                                    name: 'user',
                                    description: 'The user to remove coins from',
                                    type: 9,
                                    required: true,
                                },
                                {
                                    name: 'cpu',
                                    description: 'The amount of cpu to add',
                                    type: 4,
                                    required: true,
                                },
                                {
                                    name: 'ram',
                                    description: 'The amount of ram to add',
                                    type: 4,
                                    required: true,
                                },
                                {
                                    name: 'storage',
                                    description: 'The amount of storage to add',
                                    type: 4,
                                    required: true,
                                },
                                {
                                    name: 'slots',
                                    description: 'The amount of slots to add',
                                    type: 4,
                                    required: true,
                                }
                            ]
                        },
                        {
                            name: 'removepartner',
                            description: 'Remove a partner from the partner list',
                            options: [
                                {
                                    name: 'user',
                                    description: 'The user to remove coins from',
                                    type: 9,
                                    required: true,
                                }
                            ]
                        },
                        {
                            name: 'partners',
                            description: 'List all partners'
                        }
                    ]
            });

            console.log('Successfully reloaded application (/) commands.');
        } catch (error) {
            console.error(error);
        }
    })();
});

const partnerManager = new PartnerManager(client);

client.on('inviteCreate', async () => {
    await partnerManager.cacheInvites();
});

client.on('inviteDelete', async () => {
    await partnerManager.cacheInvites();
});

client.on('guildMemberAdd', async (member) => {
    await partnerManager.onMember(member, 'join');
});

client.on('guildBanAdd', (ban) => sendBanMessage(ban, true))
client.on('guildBanRemove', (ban) => sendBanMessage(ban, false))

client.on('guildMemberRemove', sendLeaveMessage)
client.on('messageCreate', (message) => sendPrivateMessage(message, client))
client.on('messageCreate', (message) => handleShowcaseMessage(message));

client.on('voiceStateUpdate', sendVoice);

client.on('interactionCreate', (interaction) => handleInteraction(interaction));
client.on('messageCreate', async (message) => {
    if (message.type === MessageType.GuildBoost && message.channelId === Deno.env.get("LOG_CHANNEL")! && message.guild) {
        const dbuser = await finduser(message.author.id);
        if (dbuser) {
            await addBoosterRewards(message.author.id);
            await message.channel.send(`Added booster rewards for https://discord.com/channels/${message.guildId}/${message.channelId}/${message.id}`);
        } else {
            if (message.guild.channels.cache.find((channel) => channel.name === `link-${message.author.id}`)) {
                await message.channel.send(`https://discord.com/channels/${message.guildId}/${message.channelId}/${message.id} already has a link channel`);
                return;
            }
            const channel = await message.guild.channels.create({
                name: `link-${message.author.id}`,
                parent: await message.guild.channels.fetch(Deno.env.get("LINK_CATEGORY")!) as CategoryChannel,
                permissionOverwrites: [
                    {
                        id: message.author.id,
                        allow: [ 'ViewChannel' ]
                    }
                ]
            });
            await channel?.send({ content: `<@${message.author.id}> Looks like you just boosted the server! Unfortunately, you are not linked to your BBN account yet. Please send your Email address to this channel to link your account. Our Team will respond as soon as possible.`, allowedMentions: { users: [ message.author.id ] } });
            await message.channel.send(`https://discord.com/channels/${message.guildId}/${message.channelId}/${message.id} created channel https://discord.com/channels/${message.guildId}/${channel?.id} to link`);
        }
    }
})

async function checkBoosts() {
    console.log('Checking boosts');
    const guild = await client.guilds.fetch(Deno.env.get("GUILD_ID")!);
    const log_channel = await guild.channels.fetch(Deno.env.get("LOG_CHANNEL")!);
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
            msgs.forEach((msg: Message) => {
                if (msg.type === MessageType.GuildBoost && msg.createdAt.getTime() < Date.now() - 30 * 24 * 60 * 60 * 1000) {
                    messages.push(msg);
                }
            })
        }
    }
    messages.forEach(async (msg) => {
        const user = await finduser(msg.author.id);
        if (user) {
            await removeBoosterRewards(msg.author.id);
            await msg.channel.send(`Removed booster rewards for https://discord.com/channels/${guild.id}/${msg.channelId}/${msg.id}`);
        } else {
            await msg.channel.send({ content: 'User not found, <@757969277063266407> please check this user https://discord.com/channels/${guild.id}/${msg.channelId}/${msg.id}', allowedMentions: { roles: [ '757969277063266407' ] } });
        }
    });
}

setInterval(checkBoosts, 24 * 60 * 60 * 1000);

client.login(Deno.env.get("TOKEN")!).then(() => {
    console.log('Logged in');
    checkBoosts();
    partnerManager.cacheInvites();
});