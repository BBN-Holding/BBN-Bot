import { Client, TextChannel, MessageEmbed, GuildBan, GuildMember, PartialGuildMember, User, Message, VoiceState } from 'discord.js'
//@ts-ignore
import * as config from './config.json'

export function sendBanMessage(ban: GuildBan, banned: boolean) {
    ban.client.channels.fetch(config.log_channel).then(channel => {
        const embed = defaultEmbed(ban.user);
        embed.setTitle(`${embed.title} ${banned ? '' : 'un'}banned`).addField('Reason', ban.reason ?? 'Not specified');
        (channel as TextChannel).send({ embeds: [ embed ] })
    })
}

export function handleRules(oldMember: PartialGuildMember | GuildMember, newMember: GuildMember) {
    if (oldMember.pending !== newMember.pending) {
        newMember.guild.roles.fetch(config.role_id).then(role => {
            newMember.roles.add(role!, 'Verified');
        })
        newMember.guild.channels.fetch(config.log_channel).then(channel => {
            const embed = defaultEmbed(newMember.user);
            embed.setTitle(`${embed.title} verified`).setColor('#57F287');
            (channel as TextChannel).send({ embeds: [ embed ] })
        })
    }
}

export function sendJoinMessage(member: GuildMember) {
    const embed = defaultEmbed(member.user);
    embed.setTitle(`${embed.title} joined`).setColor('#FEE75C');
    member.guild.channels.fetch(config.log_channel).then(channel => (channel as TextChannel).send({ embeds: [ embed ] }));
}

export function sendLeaveMessage(member: PartialGuildMember | GuildMember) {
    const embed = defaultEmbed(member.user);
    embed.setTitle(`${embed.title} left`)
    member.guild.channels.fetch(config.log_channel).then(channel => (channel as TextChannel).send({ embeds: [ embed ] }))
}

export function sendPrivateMessage(message: Message, client: Client) {
    if (message.channel.type == 'DM') {
        const embed = defaultEmbed(message.author);
        embed.fields[ 1 ].name = 'User ID'
        embed.setTitle("Private message received")
            .addField('\u200b', '\u200b', true)
            .addField("Mention", `<@${message.author.id}>`, true)
            .setDescription('```' + message.content + '```')
            .addField('Message ID', message.id, true)
            .setColor('#57F287');
        client.channels.fetch(config.log_channel).then(channel => (channel as TextChannel).send({ embeds: [ embed ], files: [ ...message.attachments.values() ] }))
    }
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

function sendVoiceMessage(embed: MessageEmbed, newState: VoiceState) {
    newState.guild.channels.fetch(config.voice_log_channel).then(channel => (channel as TextChannel).send({ embeds: [ embed ] }))
}

function generateVoiceEmbed(word: string, negative: boolean, newState: VoiceState, oldState: VoiceState) {
    return new MessageEmbed()
        .setTitle(`${newState.member?.user.tag!} ${word}`)
        .addField('Channel', newState.channel?.name ?? oldState.channel!.name, true)
        .addField('Members in Channel', String(newState.channel?.members.size ?? oldState.channel!.members.size), true)
        .addField('Current Time', new Date().toISOString(), true)
        .setColor(negative ? '#ED4245' : '#57F287');
}

function defaultEmbed(user: User) {
    return new MessageEmbed()
        .setTitle(((!user.bot) ? "User" : "Bot"))
        .setAuthor({ name: user.tag, iconURL: user.displayAvatarURL(), url: user.displayAvatarURL() })
        .addField(((!user.bot) ? "User" : "Bot") + " Creation Time", user.createdAt.toISOString(), true)
        .addField("ID", user.id, true)
        .setTimestamp(new Date())
        .setFooter({ text: "Provided by BBN", iconURL: "https://bbn.one/images/avatar.png" })
        .setColor('#ED4245');
}