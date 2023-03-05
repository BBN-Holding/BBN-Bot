import { ActionRowBuilder, ButtonBuilder, ButtonInteraction, ButtonStyle, EmbedBuilder, GuildMember, Interaction, TextChannel, UserSelectMenuBuilder, VoiceChannel } from "discord.js"

export async function handleInteraction(interaction: Interaction) {

    if (interaction.isButton()) {
        if (interaction.customId == "lock")
            lockVoice(interaction, true)
        else if (interaction.customId == "unlock")
            lockVoice(interaction, false)
    }

    if (interaction.isUserSelectMenu() && interaction.guild && interaction.customId === 'verify_modal') {
        const member = interaction.guild.members.cache.get(interaction.values[ 0 ])
        const role = interaction.guild.roles.cache.get("757983851032215673")

        if (member && role) {
            member?.roles.add(role, "Verified by " + interaction.user.tag)
            interaction.reply("Successfully verified <@" + interaction.values[ 0 ] + ">!")
        } else {
            interaction.reply("An error occured while assigning the role to <@" + interaction.values[ 0 ] + ">")
        }
    }

    if (!interaction.isChatInputCommand()) return

    if (interaction.commandName === 'setup') {
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
    }

    if (interaction.commandName === 'verify') {
        const verify_modal = new UserSelectMenuBuilder()
            .setCustomId("verify_modal")

        const row_username = new ActionRowBuilder<UserSelectMenuBuilder>().addComponents(verify_modal)

        await interaction.reply({ content: 'Which user do you want to verify?', components: [ row_username ], ephemeral: true })
    }
}

function lockVoice(interaction: ButtonInteraction, lock: boolean) {
    const channel = (interaction.member as GuildMember).voice.channel as VoiceChannel

    if (channel) {
        channel!.setUserLimit(lock ? channel.members.size : 0)
        interaction.reply({ content: lock ? "Successfully locked your voice channel!" : "Successfully unlocked your voice channel!", ephemeral: true })
    } else {
        interaction.reply({ content: "You have to be in a voice channel!", ephemeral: true })
    }
}