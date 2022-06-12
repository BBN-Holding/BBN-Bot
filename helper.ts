import { ChannelType } from 'discord-api-types/v10';
import { Client, TextChannel, MessageEmbed, GuildBan, GuildMember, PartialGuildMember, User, Message } from 'discord.js'
import * as config from './config.json'
import { createTransport } from 'nodemailer';

export function sendBanMessage(ban: GuildBan, banned: boolean) {
    ban.client.channels.fetch(config.log_channel).then(channel => {
        const embed = defaultEmbed(ban.user);
        embed.setTitle(`${embed.title} ${banned ? '' : 'un'}banned`).addField('Reason', ban.reason ?? 'Not specified');
        (channel as TextChannel).send({ embeds: [embed] })
    })
}

export function handleRules(oldMember: PartialGuildMember | GuildMember, newMember: GuildMember) {
    if (oldMember.pending !== newMember.pending) {
        newMember.guild.roles.fetch(config.role_id).then(role => {
            newMember.roles.add(role!, 'Verified');
        })
        newMember.guild.channels.fetch(config.log_channel).then(channel => {
            const embed = defaultEmbed(newMember.user);
            embed.setTitle(`${embed.title} verified`).setColor('GREEN');
            (channel as TextChannel).send({ embeds: [embed] })
        })
    }
}

export function sendJoinMessage(member: GuildMember) {
    const embed = defaultEmbed(member.user);
    embed.setTitle(`${embed.title} joined`).setColor('YELLOW');
    member.guild.channels.fetch(config.log_channel).then(channel => (channel as TextChannel).send({ embeds: [embed] }));
}

export function sendLeaveMessage(member: PartialGuildMember | GuildMember) {
    const embed = defaultEmbed(member.user);
    embed.setTitle(`${embed.title} left`)
    member.guild.channels.fetch(config.log_channel).then(channel => (channel as TextChannel).send({ embeds: [embed] }))
}

export function sendPrivateMessage(message: Message, client: Client) {
    if (message.channel.type == 'DM') {
        const embed = defaultEmbed(message.author);
        embed.fields[1].name = 'User ID'
        embed.setTitle("Private message received").addField('\u200b', '\u200b', true)
            .addField("Mention", `<@${message.author.id}>`, true).setDescription('```' + message.content + '```').addField('Message ID', message.id, true);
        client.channels.fetch(config.log_channel).then(channel => (channel as TextChannel).send({ embeds: [embed], files: [...message.attachments.values()] }))
    }
}

function defaultEmbed(user: User) {
    return new MessageEmbed()
        .setTitle(((!user.bot) ? "User" : "Bot"))
        .setAuthor({ name: user.tag, iconURL: user.displayAvatarURL(), url: user.displayAvatarURL() })
        .addField(((!user.bot) ? "User" : "Bot") + " Creation Time", user.createdAt.toISOString(), true)
        .addField("ID", user.id, true)
        .setTimestamp(new Date())
        .setFooter({ text: "BBN", iconURL: "https://bbn.one/images/avatar.png" })
        .setColor('RED');
}