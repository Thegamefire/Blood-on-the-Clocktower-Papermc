package com.thegamefire.bloodOnTheClocktower.votes;

import org.bukkit.Material;

import java.util.Set;

public enum VoteType {
    LIVING_VOTE(Material.WAXED_COPPER_BLOCK, Material.SHROOMLIGHT),
    GHOST_VOTE(Material.WAXED_OXIDIZED_COPPER, Material.SEA_LANTERN),
    SPENT_GHOST_VOTE(Material.POLISHED_DEEPSLATE, null);

    public Material getVoteOnBlock() {
        return voteOnBlock;
    }

    public Material getVoteOffBlock() {
        return voteOffBlock;
    }

    private final Material voteOnBlock;
    private final Material voteOffBlock;

    VoteType(Material voteOffBlock, Material voteOnBlock) {
        this.voteOffBlock = voteOffBlock;
        this.voteOnBlock = voteOnBlock;
    }

    public boolean canVote() {
        return this != SPENT_GHOST_VOTE;
    }

    public VoteType spentVote() {
        if (this == GHOST_VOTE) {
            return SPENT_GHOST_VOTE;
        }
        return LIVING_VOTE;
    }

    public static Set<Material> onBlockSet() {
        return Set.of(Material.SHROOMLIGHT, Material.SEA_LANTERN);
    }

    public static VoteType fromBlock(Material block) {
        for (VoteType voteType : VoteType.values()) {
            if ((voteType.getVoteOnBlock() != null && voteType.getVoteOnBlock().equals(block)) || voteType.getVoteOffBlock().equals(block)) {
                return voteType;
            }
        }
        return null;
    }


}
