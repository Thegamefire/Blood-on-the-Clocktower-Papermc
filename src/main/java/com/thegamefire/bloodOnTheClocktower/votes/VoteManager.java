package com.thegamefire.bloodOnTheClocktower.votes;

import com.thegamefire.bloodOnTheClocktower.BloodOnTheClocktower;
import com.thegamefire.bloodOnTheClocktower.PlayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class VoteManager {

    private static final Map<Integer, Location> voteBlocks = loadVoteBlocks();
    private static Player executee = null;
    private static int executeeScore = 0;
    private static Player nominee;
    private static int nomineeScore = 0;

    public static int getVoteBlockAnimationStepIndex() {
        return voteBlockAnimationStepIndex;
    }

    private static int voteBlockAnimationStepIndex = -1;


    public static void nominatePlayer(Player player) {
        nominee = player;
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 2, false, false, false));
        BloodOnTheClocktower.sendGlobal(
                player.playerListName()
                        .append(Component.text(" has been nominated.")));
        BloodOnTheClocktower.sendGlobal(
                Component.text(getNecessaryVoteAmount()).color(NamedTextColor.AQUA)
                        .append(Component.text(" votes are necessary to mark for execution.").color(NamedTextColor.WHITE)));
        for (Location loc : voteBlocks.values()) {
            loc.clone().add(0, -2, 0).getBlock().setType(Material.REDSTONE_BLOCK);
        }
    }

    public static void newDay() {
        executee = null;
        executeeScore = 0;
        for (Location location : voteBlocks.values()) {
            location.clone().add(0, -2, 0).getBlock().setType(Material.REDSTONE_BLOCK);
        }
    }

    public static void resetGame() {
        newDay();
        BloodOnTheClocktower.taskScheduler.runTaskLater(BloodOnTheClocktower.instance, () -> {
            for (Location location : voteBlocks.values()) {
                location.clone().add(0, 1, 0).getBlock().setType(VoteType.LIVING_VOTE.getVoteOffBlock());
            }
        }, 10);
    }

    /**
     * Toggles whether the voteBlock of the specified player is on or off
     *
     * @param houseNr Number of the specified player
     * @return Whether a voteBlock was found that could get toggled
     */
    public static boolean toggleVoteBlock(int houseNr) {
        Location voteBlock = voteBlocks.get(houseNr);
        if (voteBlock == null) {
            return false;
        }
        voteBlock = voteBlock.clone().add(0, 1, 0);
        Material blockType = voteBlock.getBlock().getType();
        VoteType type = VoteType.fromBlock(blockType);
        if (type != null) {
            Material voteOnBlock = type.getVoteOnBlock() == null ? type.getVoteOffBlock() : type.getVoteOnBlock();
            voteBlock.getBlock().setType(
                    (type.getVoteOnBlock() == blockType)
                            ? type.getVoteOffBlock()
                            : voteOnBlock
            );
            return true;
        }
        return false;
    }

    /**
     * Lowers the next VoteBlock in the Animation
     */
    public static void voteAnimationStep() {

        int nomineeNr = PlayerManager.getHouseNr(nominee);
        Optional<Integer> maxVoteBlock = voteBlocks.keySet().stream().max(Integer::compareTo);

        Location nextVoteBlock = voteBlocks.get(((nomineeNr + voteBlockAnimationStepIndex + 1) % (maxVoteBlock.get() + 1)));
        if (nextVoteBlock == null) { // Vote blocks skip a number, e.g. the houses 1, 2 and 4 exist but 3 doesn't
            voteBlockAnimationStepIndex++;
            voteAnimationStep();
        } else if (voteBlockAnimationStepIndex > maxVoteBlock.get()) { // Vote has gone around whole circle
            voteBlockAnimationStepIndex = -1;
            endVote();
        } else { // Base Case, a vote gets processed
            Block block = nextVoteBlock.clone().add(0, 1, 0).getBlock();
            if (VoteType.onBlockSet().contains(block.getType())) {
                block.setType(
                        VoteType.fromBlock(block.getType())
                                .spentVote()
                                .getVoteOffBlock()
                );
                nomineeScore++;
            }
            nextVoteBlock.clone().add(0, -2, 0).getBlock().setType(Material.AIR);
            voteBlockAnimationStepIndex++;
        }
    }

    private static void endVote() {
        boolean marked = nomineeScore > executeeScore && nomineeScore >= (PlayerManager.getPlayerNrs().size() / 2);
        BloodOnTheClocktower.sendGlobal(
                Component.text(nomineeScore).color(marked ? NamedTextColor.GREEN : NamedTextColor.RED)
                        .append(Component.text(" is "))
                        .append(Component.text(marked ? "" : "not"))
                        .append(Component.text(" enough to mark for execution.")));
        if (marked) {
            executee = nominee;
            executeeScore = nomineeScore;
            BloodOnTheClocktower.sendGlobal(executee.displayName().append(Component.text(" has been marked for execution.")));
        } else if (nomineeScore == executeeScore && executee != null) {
            BloodOnTheClocktower.sendGlobal(executee.displayName().append(Component.text(" is no longer marked for execution.")));
            executee = null;
        }
        nominee = null;
        nomineeScore = 0;
    }

    public static void startVote() {
        voteBlockAnimationStepIndex = 0;
        BloodOnTheClocktower.sendGlobal(Component.text("Starting Vote"));
        //TODO Make sure at start of game non participating players get SPENT_GHOST_VOTE Blocks
    }

    public static int getNecessaryVoteAmount() {
        return Math.max(executeeScore + 1, PlayerManager.getPlayerNrs().size() / 2);
    }

    public static void addVoteBlock(int playerNr, Location location) {
        voteBlocks.put(playerNr, location);
        location.getBlock().setType(VoteType.LIVING_VOTE.getVoteOffBlock());
        location.clone().add(0, -1, 0).getBlock().setType(Material.STICKY_PISTON);
        BlockData pistonBlockdata = location.clone().add(0, -1, 0).getBlock().getBlockData();
        ((Directional) pistonBlockdata).setFacing(BlockFace.UP);
        location.clone().add(0, -1, 0).getBlock().setBlockData(pistonBlockdata);
        BloodOnTheClocktower.debugPublic("SetBlockFacing");
        location.clone().add(0, -2, 0).getBlock().setType(Material.AIR);
        location.clone().add(0, -1, 0).getBlock().getState().update(true, true);
    }

    public static void removeVoteBlock(int playerNr) {
        voteBlocks.remove(playerNr);
    }

    public static Map<Integer, Location> getVoteBlocks() {
        return voteBlocks;
    }

    /**
     * Loads the saved VoteBlock Locations from the save
     *
     * @return Locations of the Vote Block saved in the world for each Player
     */
    public static Map<Integer, Location> loadVoteBlocks() {
        World world = Bukkit.getWorld("world");
        Map<Integer, Location> locations = new HashMap<>();
        String conf = world.getPersistentDataContainer().get(NamespacedKey.fromString("vote_blocks", BloodOnTheClocktower.instance), PersistentDataType.STRING);
        if (conf == null || conf.isEmpty()) {
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

    /**
     * Saves the locations of the Vote Blocks to the world
     */
    public static void saveVoteBlocks() {
        World world = Bukkit.getWorld("world");
        List<String> confFrame = new ArrayList<>();
        for (Integer key : voteBlocks.keySet()) {
            Location loc = voteBlocks.get(key);
            confFrame.add(key.toString() + ":" + Double.valueOf(loc.x()).intValue() + "_" + Double.valueOf(loc.y()).intValue() + "_" + Double.valueOf(loc.z()).intValue());
        }
        String conf = String.join("§", confFrame);
        world.getPersistentDataContainer().set(NamespacedKey.fromString("vote_blocks", BloodOnTheClocktower.instance), PersistentDataType.STRING, conf);
    }
}
