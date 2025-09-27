package com.thegamefire.bloodOnTheClocktower;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class PlayerManager {

    public static final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

    public PlayerManager() {
        registerTeams();
        registerObjectives();
    }

    public static void setStoryteller(Player player) {
        Team stTeam = scoreboard.getTeam("botc_storyteller");
        assert stTeam != null;
        stTeam.addPlayer(player);
    }

    public void registerTeams() {
        // Storyteller
        Team team = scoreboard.getTeam("botc_storyteller");
        team = team !=null ? team: scoreboard.registerNewTeam("botc_storyteller");
        team.prefix(Component.text("[Storyteller]").color(NamedTextColor.LIGHT_PURPLE));

        // Spectators
        team = scoreboard.getTeam("botc_spectators");
        team = team !=null ? team: scoreboard.registerNewTeam("botc_spectators");
        team.prefix(Component.text("[Spectator]").color(NamedTextColor.GRAY));
    }

    public void registerObjectives() {
        Objective houseNrs = scoreboard.getObjective("botc_house_nr");
        if (houseNrs ==null) {
            scoreboard.registerNewObjective("botc_house_nr", Criteria.DUMMY, Component.text("House Numbers"));
        }

    }

    public static void setHouse(OfflinePlayer player, int houseNr) {
        Objective houseNrs = scoreboard.getObjective("botc_house_nr");
        houseNrs.getScore(player).setScore(houseNr);
    }


}
