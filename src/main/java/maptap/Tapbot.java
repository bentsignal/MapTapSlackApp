package maptap;

import java.util.HashMap;

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
        today.updateLeaderboard(player, msg.score, msg.roundScores);
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

    public String endOfDay() {
        String currDay = java.time.LocalDate.now().getMonth() + " "
                + java.time.LocalDate.now().getDayOfMonth();
        if (!today.isToday()) {
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
