import { Client, Collection, GuildMember, Invite, TextChannel } from "discord.js";
//@ts-ignore
import * as config from './config.json'
import DB from "./db";
import { defaultEmbed } from "./helper";

export class PartnerManager {

    client: Client;
    db: DB;
    constructor(client: Client, db: DB) {
        this.client = client;
        this.db = db;
    }

    invites: Collection<string, Invite> = new Collection();
    async cacheInvites() {
        const guild = await this.client.guilds.fetch(config.guild_id);
        const invites = await guild.invites.fetch({
            cache: false
        });
        this.invites = invites;
        console.log('Cached invites');
    }

    async getLastInvite() {
        const guild = await this.client.guilds.fetch(config.guild_id);
        const invites = await guild.invites.fetch({
            cache: false
        });
        const invite = invites.find(i => this.invites.get(i.code)!.uses! !== i.uses!);
        if (!invite) return null;
        return invite.code;
    }

    async onMember(member: GuildMember, action: 'join' | 'leave') {
        let invitetext = '';
        const lastinvite = await this.getLastInvite();
        if (!lastinvite) invitetext = 'No invite found';
        else {
            const partner = await this.db.getPartnerFromInvite(lastinvite);
            if (!partner) invitetext = 'No partner found using code ' + lastinvite;
            else {
                this.db.updateLastInvite(partner.owner);
                const partnermember = await this.db.getMemberFromBBNId(partner.owner);
                if (!partnermember) invitetext = 'No member found for partner ' + partner.owner + ' using code ' + lastinvite;
                else invitetext = `<@${partnermember}> (BBN ID: ${partner.owner}) using code ${lastinvite}`;
            }
        }

        const embed = defaultEmbed(member.user);
        embed.setTitle(`${member.user.tag} ${action === 'join' ? 'joined' : 'left'}`)
            .setColor(action === 'join' ? '#FEE75C' : '#F57F7F')
            .setDescription(invitetext);
        member.guild.channels.fetch(config.log_channel).then(channel => (channel as TextChannel).send({ embeds: [embed] }));
    }
}