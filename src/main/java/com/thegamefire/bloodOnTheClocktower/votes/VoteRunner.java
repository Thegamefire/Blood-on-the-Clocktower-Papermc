package com.thegamefire.bloodOnTheClocktower.votes;

public class VoteRunner implements Runnable {
    @Override
    public void run() {
        if (VoteManager.getVoteBlockAnimationStepIndex() != -1 ) {
            VoteManager.voteAnimationStep();
        }
    }
}
