import { ActionRowBuilder, ButtonBuilder, ButtonInteraction, ButtonStyle, ChannelType, EmbedBuilder, GuildMember, GuildMemberRoleManager, Interaction, Message, ModalBuilder, PermissionsBitField, TextChannel, TextInputBuilder, TextInputStyle, UserSelectMenuBuilder, VoiceChannel } from "npm:discord.js"
import { saveTranscript, finduser, lastLogin, getServerURLs, getLastDaily, addCoins, setLastDaily, getCoins, removeCoins, addPartner, removePartner, getPartners, getMemberFromBBNId } from "./db.ts";
import { delay } from "https://deno.land/std@0.202.0/async/delay.ts";

export async function handleInteraction(interaction: Interaction) {

    if (interaction.isButton()) {
        switch (interaction.customId) {
            case "lock": {
                lockVoice(interaction, true)
                break;
            }
            case "unlock": {
                lockVoice(interaction, false)
                break;
            }
            case "create_ticket": {
                const ticket_modal = new ModalBuilder()
                    .setTitle("Kindly enter this information.")
                    .setCustomId("ticket_modal");

                const user_reason = new TextInputBuilder()
                    .setCustomId("ticket_reason")
                    .setLabel(`Why do you want to open a ticket?`)
                    .setRequired(true)
                    .setStyle(TextInputStyle.Paragraph);

                const row_user_reason = new ActionRowBuilder<TextInputBuilder>().addComponents(user_reason);
                ticket_modal.addComponents(row_user_reason);

                await interaction.showModal(ticket_modal);
                break;
            }
            case "close_ticket": {
                const channel = interaction.channel as TextChannel;
                interaction.reply({
                    content: `> We're closing your ticket. Please be patient. Ticket closed by ${interaction.user.tag}`,
                });

                // get all messages from channel
                const messages: Message[] = [];

                // Create message pointer
                let message = await channel.messages
                    .fetch({ limit: 1 })
                    .then((messagePage: any) => (messagePage.size === 1 ? messagePage.at(0) : null));

                while (message) {
                    await channel.messages
                        .fetch({ limit: 100, before: message.id })
                        .then((messagePage: any) => {
                            messagePage.forEach((msg: any) => messages.push(msg));
                            // Update our message pointer to be the last message on the page of messages
                            message = 0 < messagePage.size ? messagePage.at(messagePage.size - 1) : null;
                        });
                }
                let member;
                try {
                    member = await interaction.guild?.members.fetch(channel.name.split("-")[ 1 ]);
                } catch (_) { }
                const transcript: any = {
                    messages: [],
                    closed: `Ticket closed by ${interaction.user.tag}`,
                    with: `${member ? member.user.tag : "Unknown User"} (${channel.name.split("-")[ 1 ]})`
                };
                for (const message of messages.values()) {
                    const obj: any = {
                        author: message.author.tag,
                        authorid: message.author.id,
                        content: message.content,
                        timestamp: message.createdTimestamp,
                        avatar: message.author.displayAvatarURL(),
                    };
                    if (message.attachments.size > 0) {
                        obj.attachments = message.attachments.map((a: any) => a.url);
                    }
                    if (message.embeds.length > 0) {
                        obj.embed = message.embeds[ 0 ].toJSON();
                    }
                    transcript.messages.push(obj);
                }
                await saveTranscript(transcript)
                await channel.delete();
                break;
            }
        }
    }

    if (interaction.isUserSelectMenu() && interaction.guild && interaction.customId === 'verify_modal') {
        const member = interaction.guild.members.cache.get(interaction.values[ 0 ])
        const role = interaction.guild.roles.cache.get("757983851032215673")

        if (member && role) {
            if (member.roles.cache.has(role.id)) {
                member.roles.remove(role, `Unverified by ${interaction.user.tag}`)
                interaction.reply(`Successfully unverified <@${interaction.values[ 0 ]}>!`)
            } else {
                member.roles.add(role, `Verified by ${interaction.user.tag}`)
                interaction.reply(`Successfully verified <@${interaction.values[ 0 ]}>!`)
            }
        } else {
            interaction.reply(`An error occured while assigning the role to <@${interaction.values[ 0 ]}>`)
        }
    }

    if (interaction.isModalSubmit()) {
        try {
            const ticket_user_reason = interaction.fields.getTextInputValue("ticket_reason");
            const dbuser = await finduser(interaction.user.id);
            const ticketname = `ticket-${interaction.user.id}`;
            await interaction.guild!.channels
                .create({
                    name: ticketname,
                    type: ChannelType.GuildText,
                    topic: `ticket of ${interaction.user.tag}`,
                    parent: "1081347349462405221",

                })
                .then(async (ch: TextChannel) => {
                    const fields = [
                        {
                            name: `Reason:`,
                            value: `> ${ticket_user_reason}`,
                        }
                    ];
                    const embed = new EmbedBuilder()
                        .setColor("#5539cc")
                        .setTitle(`Ticket of ${interaction.user.username}`)
                        .addFields(fields);
                    if (dbuser) {
                        const login = await lastLogin(interaction.user.id) || [];
                        embed.addFields({
                            name: `User ID:`,
                            value: `> ${dbuser.toHexString()}`,
                        }, {
                            name: `Server URLs:`,
                            value: `> ${await getServerURLs(interaction.user.id)}`,
                        }, {
                            name: `Last Login:`,
                            value: `\`\`\`${JSON.stringify(login[ 0 ] ?? "none")}\`\`\``,
                        });
                        embed.setFooter({
                            text: login[ 1 ] ?? "No Login" as string,
                            iconURL: interaction.user.displayAvatarURL(),
                        })
                        embed.setTimestamp(new Date(new Date().toLocaleString('en-US', { timeZone: login[ 2 ] ?? "UTC" })))
                    }


                    await delay(5000)
                    ch.permissionOverwrites.create(interaction.user.id, {
                        "ViewChannel": true
                    });

                    const btnrow = new ActionRowBuilder<ButtonBuilder>().addComponents([
                        new ButtonBuilder()
                            .setCustomId(`close_ticket`)
                            .setStyle(ButtonStyle.Danger)
                            .setLabel(`Close Ticket`),
                    ]);
                    await ch.send({
                        content: `${interaction.member} || <@&1120392307087261787>`,
                        embeds: [ embed ],
                        components: [ btnrow ],
                    });
                    await interaction.reply({
                        content: `> Successfully created your ticket here: ${ch}`,
                        ephemeral: true,
                    });
                });
        } catch (e) {
            console.error(e);
        }
    }

    if (!interaction.isChatInputCommand()) return

    if (interaction.commandName === 'setup') {
        // const channel = interaction.guild?.channels.cache.get("757992735171936347") as TextChannel

        // const embed = new EmbedBuilder()
        //     .setTitle("Voicelocker")
        //     .setDescription("🔒 - Lock Voice Channel\n\n🔓 - Unlock Voice Channel")
        //     .setFooter({ text: "Provided by BBN", iconURL: "https://bbn.one/images/avatar.png" })
        //     .setColor('#f55a00')

        // const builder = new ActionRowBuilder<ButtonBuilder>().addComponents([
        //     new ButtonBuilder()
        //         .setCustomId(`lock`)
        //         .setStyle(ButtonStyle.Success)
        //         .setEmoji("🔒")
        //         .setLabel(`Lock`),
        //     new ButtonBuilder()
        //         .setCustomId(`unlock`)
        //         .setStyle(ButtonStyle.Danger)
        //         .setEmoji("🔓")
        //         .setLabel(`Unlock`),
        // ])

        // channel.send({
        //     embeds: [ embed ],
        //     components: [ builder ]
        // })

        // interaction.reply("message sent!")

        // code
        const ticketChannel = interaction.guild!.channels.cache.get("1081337337704886392") as TextChannel;
        if (!ticketChannel) return;

        const embed = new EmbedBuilder()
            .setColor("#f55a00")
            .setTitle(`BBN - Ticket Support`)
            .setDescription(`If you have a problem or question regarding BBN, create a ticket and we will get back to you as soon as possible.\ To create a ticket click the button below.`)
            .setFooter({ text: "Provided by BBN", iconURL: "https://bbn.one/images/avatar.png" })
        const btnrow = new ActionRowBuilder<ButtonBuilder>().addComponents([
            new ButtonBuilder()
                .setCustomId("create_ticket")
                .setStyle(ButtonStyle.Success)
                .setLabel("Create Ticket")

        ]);
        await ticketChannel.send({
            embeds: [ embed ],
            components: [ btnrow ],
        });

        interaction.reply({
            content: `Ticket System Setup in ${ticketChannel}`,
        });
    }

    if (interaction.commandName === 'escalate') {
        if (!(interaction.member?.roles as GuildMemberRoleManager).cache.has("1120392307087261787")) {
            interaction.reply("You do not have permission to escalate this ticket.");
            return;
        }
        // check if ticket channel
        if (!(interaction.channel?.type === ChannelType.GuildText && interaction.channel?.parent?.id === "1081347349462405221")) {
            interaction.reply("This command can only be used in a ticket channel.");
            return;
        }
        // move to escalation category
        interaction.channel?.setParent("1120395441138315345", {
            lockPermissions: false,
            reason: "Ticket escalated",
        });
        interaction.reply({
            allowedMentions: { roles: [ '757969277063266407' ] },
            content: "Ticket escalated. || <@&757969277063266407>"
        });
    }

    if (interaction.commandName === 'verify') {
        const verify_modal = new UserSelectMenuBuilder()
            .setCustomId("verify_modal")

        const row_username = new ActionRowBuilder<UserSelectMenuBuilder>().addComponents(verify_modal)

        await interaction.reply({ content: 'Which user do you want to verify?', components: [ row_username ], ephemeral: true })
    }

    if (interaction.commandName == "daily") {
        // Check if the user has already claimed their daily reward
        getLastDaily(interaction.user.id).then(async result => {
            if (result !== null) {
                // Calculate the time difference in hours
                const timeDiff = (Date.now() - result) / 3600000;
                if (timeDiff < 24) {
                    // Calculate the number of hours
                    return interaction.reply(`You have already claimed your daily reward. Please wait ${Math.ceil(24 - timeDiff)} hours before claiming again.`);
                }
            }

            // Give the user their daily reward
            const reward = 10 + (Math.floor(Math.random() * 10));
            const res = await addCoins(interaction.user.id, reward);
            if (res === null) {
                interaction.reply("We couldn't find your account. Please [log in via Discord here](<https://bbn.one/api/@bbn/auth/redirect/discord?goal=/hosting>)");
                return;
            }
            await setLastDaily(interaction.user.id, Date.now());
            interaction.reply(`You have received ${reward} coins as your daily reward!`);
        });
    }

    if (interaction.commandName == "balance") {
        // Retrieve the user's balance from the database
        const possiblemember = interaction.options.getMentionable("user", false);
        let { id } = interaction.user;
        if (possiblemember) {
            if (!interaction.memberPermissions?.has(PermissionsBitField.Flags.Administrator)) {
                interaction.reply("You do not have permission to view other users' balances.");
                return;
            }
            const member = possiblemember as GuildMember;
            id = member.id;
        }
        await getCoins(id).then(result => {
            if (result === null) {
                interaction.reply("We couldn't find your account. Please [log in via Discord here](<https://bbn.one/api/@bbn/auth/redirect/discord?goal=/hosting>)");
            } else {
                interaction.reply(`You currently have ${result} coins.`);
            }
        });
    }

    if (interaction.commandName == "addcoins") {
        if (!interaction.memberPermissions?.has(PermissionsBitField.Flags.Administrator)) {
            interaction.reply("You do not have permission to add coins.");
            return;
        }
        const member = interaction.options.getMentionable("user", true) as GuildMember;
        const coins = interaction.options.getInteger("coins", true);
        const res = await addCoins(member.id, coins);
        if (res === null) {
            interaction.reply("We couldn't find the account in our database");
            return;
        }
        interaction.reply(`Added ${coins} coins to ${member.user.username}'s balance.`);
    }

    if (interaction.commandName == "removecoins") {
        if (!interaction.memberPermissions?.has(PermissionsBitField.Flags.Administrator)) {
            interaction.reply("You do not have permission to remove coins.");
            return;
        }
        const member = interaction.options.getMentionable("user", true) as GuildMember;
        const coins = interaction.options.getInteger("coins", true);
        const res = await removeCoins(member.id, coins);
        if (res === null) {
            interaction.reply("We couldn't find the account in our database");
            return;
        }
        interaction.reply(`Removed ${coins} coins from ${member.user.username}'s balance.`);
    }

    if (interaction.commandName == "addpartner") {
        if (!interaction.memberPermissions?.has(PermissionsBitField.Flags.Administrator)) {
            interaction.reply("You do not have permission to add partners.");
            return;
        }

        const member = interaction.options.getMentionable("user", true) as GuildMember;
        const dbmember = await finduser(member.id);
        if (!dbmember) {
            interaction.reply("We couldn't find an bbn account in our database");
            return;
        }

        const cpu = interaction.options.getInteger("cpu", true);
        const ram = interaction.options.getInteger("ram", true);
        const storage = interaction.options.getInteger("storage", true);
        const slots = interaction.options.getInteger("slots", true);
        const invite = await interaction.guild?.invites.create(Deno.env.get("GETSTARTED_CHANNEL")!, {
            maxAge: 0,
            unique: true,
            reason: "Partner invite",
        });
        if (!invite) {
            interaction.reply("We couldn't create an invite for the partner");
            return;
        }
        addPartner(dbmember, cpu, ram, storage, slots, invite.code);
        interaction.reply(`Added ${member.user.username} as a partner.\n\nFollowing resources got added: \nCPU: ${cpu} \nMemory: ${ram} \nStorage: ${storage} \nSlots: ${slots} \nInvite code: https://discord.gg/${invite.code}`);
    }
    if (interaction.commandName == "removepartner") {
        if (!interaction.memberPermissions?.has(PermissionsBitField.Flags.Administrator)) {
            interaction.reply("You do not have permission to remove partners.");
            return;
        }
        const member = interaction.options.getMentionable("user", true) as GuildMember;
        const dbmember = await finduser(member.id);
        if (!dbmember) {
            interaction.reply("We couldn't find an bbn account in our database");
            return;
        }
        removePartner(dbmember);
        interaction.reply(`Removed ${member.user.username} as a partner.`);
    }
    if (interaction.commandName == "partners") {
        if (!interaction.memberPermissions?.has(PermissionsBitField.Flags.Administrator)) {
            interaction.reply("You do not have permission to list partners.");
            return;
        }
        let out = "Owner - CPU, RAM, Storage, Slots, Invitecode, last invite, uses\n";
        const partners = await getPartners();
        out += (await Promise.all(partners.map(async (partner: any) => `<@${(await interaction.guild?.members.fetch(await getMemberFromBBNId(partner.owner)))?.user.id}> (${partner.owner}) - ${partner.cpu}, ${partner.memory}, ${partner.disk}, ${partner.slots}, ${partner.invite}, <t:${Math.round(partner.lastinvite / 1000)}:R>, ${(await interaction.guild?.invites.fetch(partner.invite))?.uses}`))).join("\n");
        interaction.reply(out);
    }
}

function lockVoice(interaction: ButtonInteraction, lock: boolean) {
    const channel = (interaction.member as GuildMember).voice.channel as VoiceChannel

    if (channel) {
        channel!.setUserLimit(lock ? channel.members.size : 0)
        interaction.reply({ content: lock ? "Successfully locked your voice channel!" : "Successfully unlocked your voice channel!", ephemeral: true })
    } else
        interaction.reply({ content: "You have to be in a voice channel!", ephemeral: true })
}