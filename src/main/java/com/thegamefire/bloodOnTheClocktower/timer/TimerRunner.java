package com.thegamefire.bloodOnTheClocktower.timer;

import com.thegamefire.bloodOnTheClocktower.BloodOnTheClocktower;
import com.thegamefire.bloodOnTheClocktower.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class TimerRunner implements Runnable {

    private record HoursMinutesSeconds(int hours, int mins, int secs) {
        public static HoursMinutesSeconds fromTicks(int ticks) {
            ticks /= 20;
            ticks = ((ticks % 20) >= 10) ? ticks + 1 : ticks; // Round Up
            return new HoursMinutesSeconds(
                    ticks / 3600
                    , (ticks / 60) % 60
                    , ticks % 60
            );
        }

        @Override
        public String toString() {
            String output = "";

            output += (hours != 0) ? "%d:".formatted(hours) : "";
            output += (mins != 0 || hours != 0) ? "%02d:".formatted(mins) : "";
            output += "%02d".formatted(secs);

            return output;
        }
    }

    private static final NamespacedKey bossBarKey = NamespacedKey.fromString("botc_timer", BloodOnTheClocktower.instance);
    private static final BossBar bossBar = Bukkit.createBossBar(bossBarKey, "Timer", BarColor.YELLOW, BarStyle.SEGMENTED_20);
    private static int ticksStart = -1;
    public static int ticksLeft = -1;

    @Override
    public void run() {
        if (ticksLeft > -1) {
            HoursMinutesSeconds timeLeft = HoursMinutesSeconds.fromTicks(ticksLeft);
            double progress = (double) (ticksStart - ticksLeft + 20) / ticksStart;
            bossBar.setTitle(timeLeft.toString());
            bossBar.setProgress(Math.min(progress, 1.0));
            bossBar.setVisible(true);
            ticksLeft--;
        } else if (ticksStart > -1) {
            bossBar.setColor(
                    Math.abs(ticksLeft % 20) > 9 ?
                            BarColor.WHITE : BarColor.RED
            );
            bossBar.setTitle("Time's Up!");
            ticksLeft--;
        } else {
            bossBar.setVisible(false);
        }
    }

    /**
     * Stops the timer
     *
     * @return Whether there was a timer running
     */
    public static boolean stopTimer() {
        boolean isTimerRunning = ticksStart != -1;
        ticksStart = -1;
        ticksLeft = -1;
        bossBar.setVisible(false);
        return isTimerRunning;
    }

    /**
     * Starts a timer (a bossbar will appear with the duration left)
     *
     * @param ticks The duration of the timer
     */
    public static void startTimer(int ticks) {
        ticksStart = ticks;
        ticksLeft = ticks;
        for (Player player : PlayerManager.getPlayers()) {
            bossBar.addPlayer(player);
        }
        bossBar.setColor(BarColor.YELLOW);
    }


}
