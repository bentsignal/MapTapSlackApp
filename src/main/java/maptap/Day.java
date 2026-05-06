package maptap;

import java.util.ArrayList;

public class Day {
    protected Leaderboard leaderboard;
    protected ArrayList<Player> players;
    private String date;

    public Day(String date) {
        this.date = date;
        this.leaderboard = new Leaderboard();
        this.players = new ArrayList<>();
    }

    public boolean addPlayer(Player player) {
        if (players.contains(player)) {
            return false;
        }
        players.add(player);
        return true;
    }

    public void updateLeaderboard(Player player, int score, int[] roundScores) {
        leaderboard.update(player, score, roundScores);
    }

    public boolean isToday() {
        String currDay = java.time.LocalDate.now().getMonth() + " "
                + java.time.LocalDate.now().getDayOfMonth();
        System.out.println("Checking if " + date + " is today (" + currDay + ")...");
        return date.equals(currDay);
    }
}
