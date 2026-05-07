package maptap;

import java.time.ZoneId;
import java.util.ArrayList;

public class Day {
    protected Leaderboard leaderboard;
    protected ArrayList<Player> players;
    private String date;
    private long totalFinalScore;
    private long[] guessSums;

    public Day(String date) {
        this.date = date;
        this.leaderboard = new Leaderboard();
        this.players = new ArrayList<>();
        this.totalFinalScore = 0;
        this.guessSums = new long[5];
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

    public void recordScores(int[] guesses, int finalScore) {
        totalFinalScore += finalScore;
        for (int i = 0; i < guessSums.length && i < guesses.length; i++) {
            guessSums[i] += guesses[i];
        }
    }

    public String getDate() {
        return date;
    }

    public double averageFinalScore() {
        return players.isEmpty() ? 0.0 : (double) totalFinalScore / players.size();
    }

    public int worstGuessNumber() {
        return worstGuessIndex() + 1;
    }

    public double worstGuessAverage() {
        return players.isEmpty() ? 0.0 : (double) guessSums[worstGuessIndex()] / players.size();
    }

    private int worstGuessIndex() {
        int worstIdx = 0;
        long worstSum = guessSums[0];
        for (int i = 1; i < guessSums.length; i++) {
            if (guessSums[i] < worstSum) {
                worstSum = guessSums[i];
                worstIdx = i;
            }
        }
        return worstIdx;
    }

    public boolean isToday() {
        java.time.ZonedDateTime now = java.time.ZonedDateTime.now(ZoneId.of("America/New_York"));
        String currDay = now.getMonth() + " " + now.getDayOfMonth();
        System.out.println("Checking if " + date + " is today (" + currDay + ")...");
        return date.equals(currDay);
    }
}
