import { Client, TextChannel, GuildBan, GuildMember, PartialGuildMember, User, Message, VoiceState, EmbedBuilder } from 'discord.js'
//@ts-ignore
import * as config from './config.json'
import dns from 'dns';

export function sendBanMessage(ban: GuildBan, banned: boolean) {
    ban.client.channels.fetch(config.log_channel).then(channel => {
        const embed = defaultEmbed(ban.user);
        embed.setTitle(`${embed.data.title} ${banned ? '' : 'un'}banned`)
            .addFields([{ name: 'Reason', value: ban.reason ?? 'Not specified' }]);
        (channel as TextChannel).send({ embeds: [embed] })
    })
}
export function sendLeaveMessage(member: PartialGuildMember | GuildMember) {
    const embed = defaultEmbed(member.user);
    embed.setTitle(`${embed.data.title} left`)
    member.guild.channels.fetch(config.log_channel).then(channel => (channel as TextChannel).send({ embeds: [embed] }))
}
export function sendPrivateMessage(message: Message, client: Client) {
    if (message.channel.isDMBased()) {
        const embed = defaultEmbed(message.author);
        embed.data.fields![1].name = 'User ID'
        embed.setTitle("Private message received")
            .addFields([
                { name: '\u200b', value: '\u200b', inline: true },
                { name: "Mention", value: `<@${message.author.id}>`, inline: true },
                { name: 'Message ID', value: message.id, inline: true }
            ])
            .setDescription('```' + message.content + '```')
            .setColor('#57F287');
        client.channels.fetch(config.log_channel).then(channel => (channel as TextChannel).send({ embeds: [embed], files: [...message.attachments.values()] }))
    }
}

export async function handleShowcaseMessage(message: Message, client: Client) {
    if (message.channel.id === config.showcase_channel) {
        if (message.author.bot) return; // Ignore messages from bots
        const domainPattern = /(?:https?:\/\/)?(?:www\.)?([a-zA-Z0-9.-]+(?:\.[a-zA-Z]{2,}))(?::([0-9]+))?/;
        const match = domainPattern.exec(message.content);
        if (match) {
            const userDomain = match[1];

            if (!config.bbn_domains.includes(userDomain)) {
                const userIp = (await resolve([userDomain]))[0];
                const bbnIps = await resolve(config.bbn_domains);
                if (!bbnIps.includes(userIp)) {
                    replyAndDelete(message, `Your server \`${userDomain}\` is not hosted by BBN. Please use a BBN domain.`);
                    return;
                }
            }
            message.react('✅');
        } else {
            replyAndDelete(message, `Your message does not contain a valid domain. Please use the format \`<domain>:<port>\` or \`<domain>\`.`);
            return;
        }
    }
}

async function replyAndDelete(message: Message, content: string) {
    await message.reply(content).then(reply => setTimeout(() => reply.delete(), 10000));
    message.delete();
}

export function resolve(domains: string[]) {
    return new Promise<string[]>((resolve, reject) => {
        const resolved: string[] = [];
        domains.forEach(domain => {
            dns.resolve(domain, (err, addresses) => {
                if (err) reject(err);
                else {
                    resolved.push(...addresses);
                }
            })
        })
        resolve(resolved);
    })
}

export function sendVoice(oldState: VoiceState, newState: VoiceState) {
    if (!oldState.channel && newState.channel) {
        sendVoiceMessage(generateVoiceEmbed('joined', false, newState, oldState), newState);
    }
    if (oldState.channel && !newState.channel) {
        sendVoiceMessage(generateVoiceEmbed('left', true, newState, oldState), newState);
    }
    if (!oldState.mute && newState.mute) {
        sendVoiceMessage(generateVoiceEmbed('muted', true, newState, oldState), newState);
    }
    if (oldState.mute && !newState.mute) {
        sendVoiceMessage(generateVoiceEmbed('unmuted', false, newState, oldState), newState);
    }
    if (!oldState.deaf && newState.deaf) {
        sendVoiceMessage(generateVoiceEmbed('deafened', true, newState, oldState), newState);
    }
    if (oldState.deaf && !newState.deaf) {
        sendVoiceMessage(generateVoiceEmbed('undeafened', false, newState, oldState), newState);
    }
    if (oldState.channel && newState.channel && oldState.channelId !== newState.channelId) {
        sendVoiceMessage(generateVoiceEmbed('switched channel', true, newState, oldState).setColor('#FEE75C'), newState);
    }
}

function sendVoiceMessage(embed: EmbedBuilder, newState: VoiceState) {
    newState.guild.channels.fetch(config.voice_log_channel).then(channel => (channel as TextChannel).send({ embeds: [embed] }))
}

function generateVoiceEmbed(word: string, negative: boolean, newState: VoiceState, oldState: VoiceState) {
    return new EmbedBuilder()
        .setTitle(`${newState.member?.user.tag!} ${word}`)
        .addFields([
            { name: 'Channel', value: newState.channel?.name ?? oldState.channel!.name, inline: true },
            { name: 'Members in Channel', value: String(newState.channel?.members.size ?? oldState.channel!.members.size), inline: true },
            { name: 'Current Time', value: new Date().toISOString(), inline: true }
        ])
        .setColor(negative ? '#ED4245' : '#57F287');
}

export function defaultEmbed(user: User) {
    return new EmbedBuilder()
        .setTitle(((!user.bot) ? "User" : "Bot"))
        .setAuthor({ name: user.tag, iconURL: user.displayAvatarURL(), url: user.displayAvatarURL() })
        .addFields([
            { name: ((!user.bot) ? "User" : "Bot") + " Creation Time", value: user.createdAt.toISOString(), inline: true },
            { name: "ID", value: user.id, inline: true }
        ])
        .setTimestamp(new Date())
        .setFooter({ text: "Provided by BBN", iconURL: "https://bbn.one/images/avatar.png" })
        .setColor('#ED4245');
}