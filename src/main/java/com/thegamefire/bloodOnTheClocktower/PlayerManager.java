package com.thegamefire.bloodOnTheClocktower;

import com.thegamefire.bloodOnTheClocktower.votes.VoteType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

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
        team = team != null ? team : scoreboard.registerNewTeam("botc_storyteller");
        team.prefix(Component.text("[Storyteller]").color(NamedTextColor.LIGHT_PURPLE));

        // Spectators
        team = scoreboard.getTeam("botc_spectators");
        team = team != null ? team : scoreboard.registerNewTeam("botc_spectators");
        team.prefix(Component.text("[Spectator]").color(NamedTextColor.GRAY));

        team = scoreboard.getTeam("botc_ghosts");
        team = team != null ? team : scoreboard.registerNewTeam("botc_ghosts");
        team.prefix(Component.text("\uD83D\uDC7B").color(NamedTextColor.DARK_AQUA));
        team.suffix(Component.text("\uD83D\uDC7B").color(NamedTextColor.DARK_AQUA));
    }

    public void registerObjectives() {
        Objective objective = scoreboard.getObjective("botc_house_nr");
        if (objective == null) {
            scoreboard.registerNewObjective("botc_house_nr", Criteria.DUMMY, Component.text("House Numbers"));
        }
        objective = scoreboard.getObjective("botc_vote_available");
        if (objective == null) {
            scoreboard.registerNewObjective("botc_vote_available", Criteria.DUMMY, Component.text("Vote Available"));
        }

    }

    public static int getHouseNr(OfflinePlayer player) {
        Objective objective = scoreboard.getObjective("botc_house_nr");
        assert objective != null;
        return objective.getScore(player).getScore();
    }

    public static void setHouse(OfflinePlayer player, int houseNr) {
        Objective objective = scoreboard.getObjective("botc_house_nr");
        assert objective != null;
        objective.getScore(player).setScore(houseNr);
    }

    public static void setVoteType(OfflinePlayer player, VoteType vote) {
        Objective objective = scoreboard.getObjective("botc_vote_available");
        objective.getScore(player).setScore(vote.ordinal());
    }

    public static VoteType getVoteAvailable(OfflinePlayer player) {
        Objective objective = scoreboard.getObjective("botc_vote_available");
        return VoteType.values()[objective.getScore(player).getScore()];
    }

    public static List<Integer> getPlayerNrs() {
        List<Integer> output = new ArrayList<>();
        Objective objective = scoreboard.getObjective("botc_house_nr");
        for (String entry : scoreboard.getEntries()) {
            Score score = objective.getScore(entry);
            if (score.isScoreSet()) {
                output.add(score.getScore());
            }
        }
        return output;
    }


}
