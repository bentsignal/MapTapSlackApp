package maptap;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Tapbot {

    private HashMap<String, Day> days;
    private HashMap<String, Player> players;
    private Leaderboard allTimeLeaderboard;
    private Day today;

    private Player highScorePlayer;
    private int highScore;
    private Player lowScorePlayer;
    private int lowScore;

    public Tapbot() {
        this.days = new HashMap<>();
        this.players = new HashMap<>();
        this.allTimeLeaderboard = new Leaderboard();
        lowScore = Integer.MAX_VALUE;
        highScore = 0;
    }

    public void addMsg(String body, String name) {
        Message msg = new Message(body, name);
        today = days.get(msg.date);
        if (today == null) {
            today = new Day(msg.date);
            days.put(msg.date, today);
        }
        Player player = players.get(msg.name);
        if (player == null) {
            player = new Player(msg.name);
            players.put(msg.name, player);
        }
        if (!today.addPlayer(player)) { // check if player has already sent today's score
            return;
        }
        player.addScore(msg.score);
        today.updateLeaderboard(player, msg.score);
        today.recordScores(msg.guesses, msg.score);
        allTimeLeaderboard.update(player, player.totalScore);

        if (msg.score > highScore) {
            highScore = msg.score;
            highScorePlayer = player;
        }
        if (msg.score < lowScore) {
            lowScore = msg.score;
            lowScorePlayer = player;
        }
    }

    public String stats() {
        StringBuffer sb = new StringBuffer();
        sb.append("Executing /stats\n");
        if (highScorePlayer == null || lowScorePlayer == null) {
            sb.append("No stats to display");
            return sb.toString();
        }
        sb.append("All time stats\n");
        sb.append("Best round: ")
                .append(highScore)
                .append(" held by <@")
                .append(highScorePlayer.name)
                .append(">\n");
        sb.append("Worst round: ")
                .append(lowScore)
                .append(" held by <@")
                .append(lowScorePlayer.name)
                .append(">\n");
        return sb.toString();
    }

    public String playerStats(String name) {
        String cmd = "Executing /playerstats <<@" + name + ">>\n";
        Player player = players.get(name);
        if (player == null) return cmd + "Player has no rounds to log";
        else return cmd + player.getStats();
    }

    public String worstLocations() {
        StringBuilder sb = new StringBuilder();
        sb.append("Executing /worst-locations\n");
        if (days.isEmpty()) {
            sb.append("No locations to display");
            return sb.toString();
        }
        List<Day> sorted = new ArrayList<>(days.values());
        sorted.sort((a, b) -> Double.compare(a.averageFinalScore(), b.averageFinalScore()));
        sb.append("Five worst locations by average score:\n");
        int n = Math.min(5, sorted.size());
        for (int i = 0; i < n; i++) {
            Day d = sorted.get(i);
            sb.append(i + 1)
                    .append(". ")
                    .append(d.getDate().toLowerCase())
                    .append(" — worst guess: #")
                    .append(d.worstGuessNumber())
                    .append(" — avg: ")
                    .append(String.format("%.1f", d.worstGuessAverage()))
                    .append("\n");
        }
        return sb.toString();
    }

    public String averages() {
        StringBuilder sb = new StringBuilder();
        sb.append("Executing /averages\n");
        if (players.isEmpty()) {
            sb.append("No averages to display");
            return sb.toString();
        }
        sb.append("Average scores:\n");
        players.values().stream()
                .sorted(Comparator.comparingInt((Player p) -> p.averageScore).reversed())
                .forEach(p -> sb.append("<@")
                        .append(p.name)
                        .append(">: ")
                        .append(p.averageScore)
                        .append("\n"));
        return sb.toString();
    }

    public String endOfDay() {
        java.time.ZonedDateTime now = java.time.ZonedDateTime.now(ZoneId.of("America/New_York"));
        String currDay = now.getMonth() + " " + now.getDayOfMonth();
        if (today == null || !today.isToday()) {
            today = days.get(currDay);
        }
        if (today == null) {
            return "No scores for today, " + currDay + "!";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Today's leaderboard:\n");
        sb.append(today.leaderboard.toString()).append("\n\n");
        sb.append("All time leaderboard:\n");
        sb.append(allTimeLeaderboard.toString());
        return sb.toString();
    }
}
