import { ActionRowBuilder, ButtonBuilder, ButtonInteraction, ButtonStyle, ChannelType, EmbedBuilder, GuildMember, Interaction, ModalBuilder, PermissionsBitField, TextChannel, TextInputBuilder, TextInputStyle, UserSelectMenuBuilder, VoiceChannel } from "discord.js"
import DB from "./sqlite";

export async function handleInteraction(interaction: Interaction, db: DB) {

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
                    .setLabel(`Why do you want to open a ticket?`.substring(0, 45))
                    .setMinLength(30)
                    .setMaxLength(250)
                    .setRequired(true)
                    .setStyle(TextInputStyle.Paragraph);

                const row_user_reason = new ActionRowBuilder<TextInputBuilder>().addComponents(user_reason);
                ticket_modal.addComponents(row_user_reason);

                await interaction.showModal(ticket_modal);
                break;
            }
            case "delete_ticket": {
                let channel = interaction.channel as TextChannel;
                interaction.reply({
                    content: `> We're closing your ticket. Please be patient.`,
                });
                setTimeout(() => {
                    channel.setParent("1082672434777428128", { lockPermissions: true });
                }, 5000);
                break;
            }
        }
    }

    if (interaction.isUserSelectMenu() && interaction.guild && interaction.customId === 'verify_modal') {
        const member = interaction.guild.members.cache.get(interaction.values[ 0 ])
        const role = interaction.guild.roles.cache.get("757983851032215673")

        if (member && role) {
            member.roles.add(role, "Verified by " + interaction.user.tag)
            interaction.reply("Successfully verified <@" + interaction.values[ 0 ] + ">!")
        } else {
            interaction.reply("An error occured while assigning the role to <@" + interaction.values[ 0 ] + ">")
        }
    }

    if (interaction.isModalSubmit()) {
        const ticket_user_reason = interaction.fields.getTextInputValue("ticket_reason");

        let ticketname = `ticket-${interaction.user.id}`;
        await interaction.guild!.channels
            .create({
                name: ticketname,
                type: ChannelType.GuildText,
                topic: `ticket of ${interaction.user.tag}`,
                parent: "1081347349462405221",

            })
            .then(async (ch: TextChannel) => {
                let embed = new EmbedBuilder()
                    .setColor("#5539cc")
                    .setTitle(`Ticket of ${interaction.user.username}`)
                    .addFields([
                        {
                            name: `Reason:`,
                            value: `> ${ticket_user_reason}`,
                        }
                    ]);

                ch.permissionOverwrites.create(interaction.user.id, {
                    "ViewChannel": true
                })

                let btnrow = new ActionRowBuilder<ButtonBuilder>().addComponents([
                    new ButtonBuilder()
                        .setCustomId(`delete_ticket`)
                        .setStyle(ButtonStyle.Danger)
                        .setLabel(`Delete Ticket`),
                ]);
                ch.send({
                    content: `${interaction.member} || <@&757969277063266407>`,
                    embeds: [ embed ],
                    components: [ btnrow ],
                });
                interaction.reply({
                    content: `> Successfully created your ticket here: ${ch}`,
                    ephemeral: true,
                });
            });
    }

    if (!interaction.isChatInputCommand()) return

    /*if (interaction.commandName === 'setup') {
        const channel = interaction.guild?.channels.cache.get("788844358592888863") as TextChannel

        const embed = new EmbedBuilder()
            .setTitle("Voicelocker")
            .setDescription("🔒 - Lock Voice Channel\n\n🔓 - Unlock Voice Channel")
            .setFooter({ text: "Provided by BBN", iconURL: "https://bbn.one/images/avatar.png" })
            .setColor('#f55a00')

        const builder = new ActionRowBuilder<ButtonBuilder>().addComponents([
            new ButtonBuilder()
                .setCustomId(`lock`)
                .setStyle(ButtonStyle.Success)
                .setEmoji("🔒")
                .setLabel(`Lock`),
            new ButtonBuilder()
                .setCustomId(`unlock`)
                .setStyle(ButtonStyle.Danger)
                .setEmoji("🔓")
                .setLabel(`Unlock`),
        ])

        channel.send({
            embeds: [ embed ],
            components: [ builder ]
        })

        interaction.reply("message sent!")

        // code
        let ticketChannel = interaction.guild!.channels.cache.get("1081337337704886392") as TextChannel;
        if (!ticketChannel) return;

        let embed = new EmbedBuilder()
            .setColor("#f55a00")
            .setTitle(`mc4u.xyz - Ticket Support`)
            .setDescription(`If you have a problem or question regarding mc4u, create a ticket and we will get back to you as soon as possible.\ To create a ticket click the button below.`)
            .setFooter({ text: "Provided by BBN", iconURL: "https://bbn.one/images/avatar.png" })
        let btnrow = new ActionRowBuilder<ButtonBuilder>().addComponents([
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
    }*/

    if (interaction.commandName === 'verify') {
        const verify_modal = new UserSelectMenuBuilder()
            .setCustomId("verify_modal")

        const row_username = new ActionRowBuilder<UserSelectMenuBuilder>().addComponents(verify_modal)

        await interaction.reply({ content: 'Which user do you want to verify?', components: [ row_username ], ephemeral: true })
    }

    if (interaction.commandName == "daily") {
        // Check if the user has already claimed their daily reward
        db.getLastDaily(interaction.user.id).then(async result => {
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
            interaction.reply(`You have received ${reward} coins as your daily reward!`);
            await db.addCoins(interaction.user.id, reward);
            await db.setLastDaily(interaction.user.id, Date.now());
        });
    }

    if (interaction.commandName == "balance") {
        // Retrieve the user's balance from the database
        const possiblemember = interaction.options.getMentionable("user", false);
        let id = interaction.user.id;
        if (possiblemember) {
            if (!interaction.memberPermissions?.has(PermissionsBitField.Flags.Administrator)) {
                interaction.reply("You do not have permission to view other users' balances.");
                return;
            }
            const member = possiblemember as GuildMember;
            id = member.id;
        }
        await db.getCoins(id).then(result => {
            if (result !== null) {
                interaction.reply(`Your balance is ${result} coins.`);
            } else {
                interaction.reply("You have not claimed your daily reward yet. Use the daily command to claim your reward and start earning coins.");
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
        await db.addCoins(member.id, coins);
        interaction.reply(`Added ${coins} coins to ${member.user.username}'s balance.`);
    }

    if (interaction.commandName == "removecoins") {
        if (!interaction.memberPermissions?.has(PermissionsBitField.Flags.Administrator)) {
            interaction.reply("You do not have permission to remove coins.");
            return;
        }
        const member = interaction.options.getMentionable("user", true) as GuildMember;
        const coins = interaction.options.getInteger("coins", true);
        await db.removeCoins(member.id, coins);
        interaction.reply(`Removed ${coins} coins from ${member.user.username}'s balance.`);
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