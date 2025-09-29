package com.thegamefire.bloodOnTheClocktower;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteManager {

    private static Map<Integer, Location> voteBlocks = loadVoteBlocks();
    private static Player executee = null;
    private static int executeeScore = 0;
    private static Player nominee;


    public void nominatePlayer(Player player) {
        nominee = player;
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 2, false, false, false));
    }

    public static void newDay() {
        executee = null;
        executeeScore = 0;
    }

    public static void addVoteBlock(int playerNr, Location location) {
        voteBlocks.put(playerNr, location);
    }

    public static void removeVoteBlock(int playerNr) {
        voteBlocks.remove(playerNr);
    }

    public static Map<Integer, Location> loadVoteBlocks() {
        World world = Bukkit.getWorld("world");
        Map<Integer, Location> locations = new HashMap<>();
        String conf = world.getPersistentDataContainer().get(NamespacedKey.fromString("vote_blocks", BloodOnTheClocktower.instance), PersistentDataType.STRING);
        if (conf == null) {
            return locations;
        }
        for (String locString : conf.split("§")) {
            String[] keyVal = locString.split(":");
            String[] coords = keyVal[1].split("_");
            Location loc = new Location(world, Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
            locations.put(Integer.parseInt(keyVal[0]), loc);
        }
        return locations;
    }

    public static void saveVoteBlocks() {
        World world = Bukkit.getWorld("world");
        List<String> confFrame = new ArrayList<>();
        for (Integer key : voteBlocks.keySet()) {
            Location loc = voteBlocks.get(key);
            confFrame.add(key.toString() + ":" + loc.x() + "_" + loc.y() + "_" + loc.z());
        }
        String conf = String.join("§", confFrame);
        world.getPersistentDataContainer().set(NamespacedKey.fromString("vote_blocks", BloodOnTheClocktower.instance), PersistentDataType.STRING, conf);
    }
}
