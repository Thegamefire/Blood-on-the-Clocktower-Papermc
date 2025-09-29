package com.thegamefire.bloodOnTheClocktower;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;

public final class BloodOnTheClocktower extends JavaPlugin {

    private static PlayerManager playerManager;
    public static BloodOnTheClocktower instance;

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        Bukkit.getWorld("world").setGameRule(GameRule.KEEP_INVENTORY, true);
        playerManager = new PlayerManager();
        getServer().getPluginManager().registerEvents(new TokenPlaceListener(), this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, command -> {
            Commands commands = command.registrar();
            commands.register(BotcCommand.createCommand());
        });


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        VoteManager.saveVoteBlocks();
    }
}
