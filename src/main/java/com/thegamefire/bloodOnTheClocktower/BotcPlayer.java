package com.thegamefire.bloodOnTheClocktower;

import org.bukkit.OfflinePlayer;

public class BotcPlayer {

    private final OfflinePlayer player;
    private int houseNr;

    public BotcPlayer(OfflinePlayer player, int houseNr) {
        this.player = player;
        this.houseNr = houseNr;
    }

    public BotcPlayer(OfflinePlayer player) {
        this.player = player;
        this.houseNr = -1;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public int getHouseNr() {
        return houseNr;
    }

    public void setHouseNr(int houseNr) {
        this.houseNr = houseNr;
    }
}
