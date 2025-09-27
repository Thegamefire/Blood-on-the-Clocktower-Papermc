package com.thegamefire.bloodOnTheClocktower;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class TokenPlaceListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getPersistentDataContainer().has(NamespacedKey.fromString("botc_char", BloodOnTheClocktower.instance))) {
            event.setCancelled(true);
        }
    }
}
