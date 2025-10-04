package com.thegamefire.bloodOnTheClocktower;

import com.thegamefire.bloodOnTheClocktower.votes.VoteManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteLeverListener implements Listener {

    private static final Map<Location, Integer> voteLevers = loadVoteLevers();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !event.getAction().isRightClick()) {
            return;
        }
        Location loc = event.getClickedBlock().getLocation();
        Integer houseNr = voteLevers.get(loc);
        if (houseNr != null) {
            event.setCancelled(!VoteManager.toggleVoteBlock(houseNr));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (voteLevers.containsKey(event.getBlock().getLocation())) {
            removeVoteLever(event.getBlock().getLocation());
            BloodOnTheClocktower.debugPublic("Removed Votelever");
        }
    }

    public static void addVoteLever(Location location, int houseNr) {
        voteLevers.put(location, houseNr);
    }

    public static void removeVoteLever(Location location) {
        voteLevers.remove(location);
    }

    public static Map<Location, Integer> loadVoteLevers() {
        World world = Bukkit.getWorld("world");
        Map<Location, Integer> locations = new HashMap<>();
        String conf = world.getPersistentDataContainer().get(NamespacedKey.fromString("vote_levers", BloodOnTheClocktower.instance), PersistentDataType.STRING);
        if (conf == null) {
            return locations;
        }
        for (String locString : conf.split("§")) {
            String[] keyVal = locString.split(":");
            String[] coords = keyVal[1].split("_");
            Location loc = new Location(world, Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
            locations.put(loc, Integer.parseInt(keyVal[0]));
        }
        return locations;
    }

    /**
     * Saves the locations of the Vote Blocks to the world
     */
    public static void saveVoteLevers() {
        World world = Bukkit.getWorld("world");
        List<String> confFrame = new ArrayList<>();
        for (Location loc : voteLevers.keySet()) {
            Integer number = voteLevers.get(loc);
            confFrame.add(number.toString() + ":" + Double.valueOf(loc.x()).intValue() + "_" + Double.valueOf(loc.y()).intValue() + "_" + Double.valueOf(loc.z()).intValue());
        }
        String conf = String.join("§", confFrame);
        world.getPersistentDataContainer().set(NamespacedKey.fromString("vote_levers", BloodOnTheClocktower.instance), PersistentDataType.STRING, conf);
    }
}
