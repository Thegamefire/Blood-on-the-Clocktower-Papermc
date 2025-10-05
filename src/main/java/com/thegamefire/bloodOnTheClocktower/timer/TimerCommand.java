package com.thegamefire.bloodOnTheClocktower.timer;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TimerCommand {

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("timer-botc")
                .then(Commands.literal("mins")
                        .then(Commands.argument("minutes", IntegerArgumentType.integer(1))
                                .executes(TimerCommand::startTimerMins)))
                .then(Commands.literal("secs")
                        .then(Commands.argument("seconds", IntegerArgumentType.integer(1))
                                .executes(TimerCommand::startTimerSecs)))
                .then(Commands.literal("stop")
                        .executes(TimerCommand::stopTimer))
                .build();
    }

    private static int startTimerMins(CommandContext<CommandSourceStack> ctx) {
        int minutes = IntegerArgumentType.getInteger(ctx, "minutes");
        TimerRunner.startTimer(minutes * 60 * 20);
        ctx.getSource().getSender().sendMessage(Component.text("Timer started for %d minutes.".formatted(minutes)));
        return Command.SINGLE_SUCCESS;
    }

    private static int startTimerSecs(CommandContext<CommandSourceStack> ctx) {
        int secs = IntegerArgumentType.getInteger(ctx, "seconds");
        TimerRunner.startTimer(secs * 20);
        ctx.getSource().getSender().sendMessage(Component.text("Timer started for %d seconds.".formatted(secs)));
        return Command.SINGLE_SUCCESS;
    }

    private static int stopTimer(CommandContext<CommandSourceStack> ctx) {
        if (TimerRunner.stopTimer()) {
            ctx.getSource().getSender().sendMessage(Component.text("Stopped timer."));
        } else {
            ctx.getSource().getSender().sendMessage(Component.text("No timers running").color(NamedTextColor.RED));
        }
        return Command.SINGLE_SUCCESS;
    }


}
