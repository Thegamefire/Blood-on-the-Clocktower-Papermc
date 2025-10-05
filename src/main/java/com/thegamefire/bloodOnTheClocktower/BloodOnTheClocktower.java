package com.thegamefire.bloodOnTheClocktower;

import com.thegamefire.bloodOnTheClocktower.timer.TimerCommand;
import com.thegamefire.bloodOnTheClocktower.timer.TimerRunner;
import com.thegamefire.bloodOnTheClocktower.votes.VoteLeverListener;
import com.thegamefire.bloodOnTheClocktower.votes.VoteManager;
import com.thegamefire.bloodOnTheClocktower.votes.VoteRunner;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class BloodOnTheClocktower extends JavaPlugin {

    private static PlayerManager playerManager;
    public static BloodOnTheClocktower instance;
    public static BukkitScheduler taskScheduler;

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        Bukkit.getWorld("world").setGameRule(GameRule.KEEP_INVENTORY, true);
        playerManager = new PlayerManager();
        getServer().getPluginManager().registerEvents(new TokenPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new VoteLeverListener(), this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, command -> {
            Commands commands = command.registrar();
            commands.register(BotcCommand.createCommand());
            commands.register(TimerCommand.createCommand());
        });
        BukkitScheduler scheduler = this.getServer().getScheduler();
        scheduler.runTaskTimer(this, new VoteRunner(), 1, 20);
        scheduler.runTaskTimer(this, new TimerRunner(), 1, 1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        VoteManager.saveVoteBlocks();
        VoteLeverListener.saveVoteLevers();
    }

    public static void sendGlobal(Component message) {
        Bukkit.getServer().sendMessage(message);
    }

    public static void debugPublic(String to_be_sent) {
        Bukkit.getConsoleSender().sendMessage(
                Component.text("[BOTC_DEBUG] ")
                        .color(NamedTextColor.LIGHT_PURPLE)
                        .decorate(TextDecoration.BOLD)
                        .append(Component.text(to_be_sent).color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false))
        );
    }
}
