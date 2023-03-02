import { ActionRowBuilder, Interaction, UserSelectMenuBuilder } from "discord.js";

export async function handleInteraction(interaction: Interaction) {

    //TODO: VOICE LOCKER
    if (interaction.isModalSubmit()) {

    }

    if (interaction.isUserSelectMenu() && interaction.guild && interaction.customId === 'verify_modal') {
        const member = interaction.guild.members.cache.get(interaction.values[ 0 ]);
        const role = interaction.guild.roles.cache.get("757983851032215673");

        if (member && role) {
            member?.roles.add(role, "Verified by " + interaction.user.tag);
            interaction.reply("Successfully verified <@" + interaction.values[ 0 ] + ">!")
        } else {
            interaction.reply("An error occured while assigning the role to <@" + interaction.values[ 0 ] + ">")
        }
    }

    if (!interaction.isChatInputCommand()) return;

    if (interaction.commandName === 'ping') {
        await interaction.reply('Pong!');
    }

    if (interaction.commandName === 'verify') {
        const verify_modal = new UserSelectMenuBuilder()
            .setCustomId("verify_modal")

        const row_username = new ActionRowBuilder<UserSelectMenuBuilder>().addComponents(verify_modal);

        await interaction.reply({ content: 'Which user do you want to verify?', components: [ row_username ] })
    }
}