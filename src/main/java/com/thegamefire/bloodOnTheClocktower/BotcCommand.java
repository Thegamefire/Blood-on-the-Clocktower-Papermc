package com.thegamefire.bloodOnTheClocktower;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.thegamefire.bloodOnTheClocktower.characters.BotcCharacter;
import com.thegamefire.bloodOnTheClocktower.characters.BotcCharacterArgument;
import com.thegamefire.bloodOnTheClocktower.votes.VoteManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class BotcCommand {

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("botc")
                .then(Commands.literal("players")
                        .then(Commands.literal("assign")
                                .then(Commands.argument("player", ArgumentTypes.player())
                                        .then(Commands.argument("house_number", IntegerArgumentType.integer(0))
                                                .executes(BotcCommand::assignHouse))
                                        .then(Commands.literal("storyteller")
                                                .executes(BotcCommand::setStoryteller))))
                        .then(Commands.literal("give-character")
                                .then(Commands.argument("player", ArgumentTypes.player())
                                        .then(Commands.argument("character", new BotcCharacterArgument())
                                                .executes(BotcCommand::giveCharacter)))))
                .then(Commands.literal("vote")
                        .then(Commands.literal("nominate")
                                .then(Commands.argument("player", ArgumentTypes.player())
                                        .executes(BotcCommand::nominate)))
                        .then(Commands.literal("start")
                                .executes(BotcCommand::startVote))
                        .then(Commands.literal("execute")))
                .then(Commands.literal("setup")
                        .then(Commands.literal("voteblock")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("location", ArgumentTypes.blockPosition())
                                                .then(Commands.argument("player_number", IntegerArgumentType.integer(0))
                                                        .executes(BotcCommand::setVoteBlock))))
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("player_number", IntegerArgumentType.integer(0))
                                                .executes(BotcCommand::removeVoteBlock)))
                                .then(Commands.literal("list")
                                        .executes(BotcCommand::listVoteBlocks)))
                        .then(Commands.literal("votelever")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("location", ArgumentTypes.blockPosition())
                                                .then(Commands.argument("player_number", IntegerArgumentType.integer(0))
                                                        .executes(BotcCommand::setVoteLever))))))
                .build();
    }

    private static int setVoteLever(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Location loc = ctx.getArgument("location", BlockPositionResolver.class).resolve(ctx.getSource()).toLocation(Bukkit.getWorld("world"));
        int playerNr = IntegerArgumentType.getInteger(ctx, "player_number");
        VoteLeverListener.addVoteLever(loc, playerNr);
        return Command.SINGLE_SUCCESS;
    }

    private static int listVoteBlocks(CommandContext<CommandSourceStack> ctx) {
        Map<Integer, Location> voteBlocks = VoteManager.getVoteBlocks();
        ctx.getSource().getSender().sendMessage(Component.text("Vote blocks are:", NamedTextColor.YELLOW));
        for (int playerNr : voteBlocks.keySet()) {
            Location loc = voteBlocks.get(playerNr);
            ctx.getSource().getSender().sendMessage(
                    Component.text(playerNr)
                            .append(Component.text(": "))
                            .append(Component.text(String.format("%g, %g, %g", loc.x(), loc.y(), loc.z())))
            );
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int startVote(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        VoteManager.startVote();
        return Command.SINGLE_SUCCESS;
    }

    private static int nominate(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
        VoteManager.nominatePlayer(player);
        return Command.SINGLE_SUCCESS;
    }

    private static int assignHouse(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
        int houseNr = IntegerArgumentType.getInteger(ctx, "house_number");
        PlayerManager.setHouse(player, houseNr);
        return Command.SINGLE_SUCCESS;
    }


    private static int setStoryteller(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player storyteller = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
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

    private static int setVoteBlock(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Location loc = ctx.getArgument("location", BlockPositionResolver.class).resolve(ctx.getSource()).toLocation(Bukkit.getWorld("world"));
        int playerNr = IntegerArgumentType.getInteger(ctx, "player_number");
        VoteManager.addVoteBlock(playerNr, loc);
        Bukkit.getConsoleSender().sendMessage(Component.text(String.format("Added VoteBlock at %g, %g, %g for player %d", loc.x(), loc.y(), loc.z(), playerNr)));
        return Command.SINGLE_SUCCESS;
    }

    private static int removeVoteBlock(CommandContext<CommandSourceStack> ctx) {
        int playerNr = IntegerArgumentType.getInteger(ctx, "player_number");
        VoteManager.removeVoteBlock(playerNr);
        Bukkit.getConsoleSender().sendMessage(Component.text(String.format("Removed VoteBlock for player %d", playerNr)));
        return Command.SINGLE_SUCCESS;
    }


}
