package com.thegamefire.bloodOnTheClocktower;

import com.thegamefire.bloodOnTheClocktower.votes.VoteLeverListener;
import com.thegamefire.bloodOnTheClocktower.votes.VoteManager;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.WorldSaveEvent;

public class TokenPlaceListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getPersistentDataContainer().has(NamespacedKey.fromString("botc_char", BloodOnTheClocktower.instance))) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        //TODO Move in Code Cleanup
        VoteManager.saveVoteBlocks();
        VoteLeverListener.saveVoteLevers();
    }
}
