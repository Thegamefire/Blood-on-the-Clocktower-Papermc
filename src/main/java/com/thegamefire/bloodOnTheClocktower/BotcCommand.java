package com.thegamefire.bloodOnTheClocktower;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.thegamefire.bloodOnTheClocktower.characters.BotcCharacter;
import com.thegamefire.bloodOnTheClocktower.characters.BotcCharacterArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class BotcCommand {

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("botc")
                .then(Commands.literal("set-storyteller")
                        .requires(sender -> sender.getSender().hasPermission("botc.manage-game"))
                        .executes(BotcCommand::setStoryteller)
                        .then(Commands.argument("storyteller", ArgumentTypes.player())
                                .executes(BotcCommand::setOtherStoryteller)))
                .then(Commands.literal("assign")
                        .requires(sender -> sender.getSender().hasPermission("botc.manage-game"))
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .then(Commands.argument("house_number", IntegerArgumentType.integer(0))
                                        .executes(BotcCommand::assignHouse))))
                .then(Commands.literal("give-character")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .then(Commands.argument("character", new BotcCharacterArgument())
                                        .executes(BotcCommand::giveCharacter))))
                .build();
    }

    private static int assignHouse(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
        int houseNr = IntegerArgumentType.getInteger(ctx, "house_number");
        PlayerManager.setHouse(player, houseNr);
        return Command.SINGLE_SUCCESS;
    }

    private static int setStoryteller(CommandContext<CommandSourceStack> ctx) {
        Entity executor = ctx.getSource().getExecutor();
        if ( executor instanceof Player player) {
            PlayerManager.setStoryteller(player);
        } else {
            ctx.getSource().getSender().sendMessage(Component.text("No player specified"));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int setOtherStoryteller(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player storyteller = ctx.getArgument("storyteller", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
        PlayerManager.setStoryteller(storyteller);
        return Command.SINGLE_SUCCESS;
    }

    private static int giveCharacter(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
        BotcCharacter character = ctx.getArgument("character", BotcCharacter.class);

        ItemStack token = ItemStack.of(Material.SUNFLOWER);
        token.editPersistentDataContainer(pdc -> {
            pdc.set(NamespacedKey.fromString("botc_char", BloodOnTheClocktower.instance), PersistentDataType.STRING, character.name().toLowerCase());
        });
        token.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addFloat(1200.5f).build());
        token.setData(DataComponentTypes.CUSTOM_NAME,
                Component.text(character.getName())
                        .color(character.getType().getColor())
                        .decorate(TextDecoration.BOLD)
                        .decoration(TextDecoration.ITALIC, false));
        player.give(token);

        return Command.SINGLE_SUCCESS;
    }


}
