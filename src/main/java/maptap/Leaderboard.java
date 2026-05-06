package maptap;

public class Leaderboard {
    private int topScore;
    private Player topPlayer = null;
    private int[] topRoundScores;
    private int secondScore;
    private Player secondPlayer = null;
    private int[] secondRoundScores;
    private int thirdScore;
    private Player thirdPlayer = null;
    private int[] thirdRoundScores;

    private boolean beats(int score, int[] roundScores, int otherScore, int[] otherRoundScores) {
        if (score != otherScore) return score > otherScore;
        if (roundScores == null || otherRoundScores == null) return false;
        for (int i = 4; i >= 0; i--) {
            if (roundScores[i] != otherRoundScores[i]) return roundScores[i] > otherRoundScores[i];
        }
        return false;
    }

    public void update(Player player, int score, int[] roundScores) {
        if (beats(score, roundScores, topScore, topRoundScores)) {
            if (player == topPlayer) {
                topScore = score;
                topRoundScores = roundScores;
                return;
            }
            thirdScore = secondScore;
            thirdPlayer = secondPlayer;
            thirdRoundScores = secondRoundScores;
            secondScore = topScore;
            secondPlayer = topPlayer;
            secondRoundScores = topRoundScores;
            topScore = score;
            topPlayer = player;
            topRoundScores = roundScores;
        } else if (beats(score, roundScores, secondScore, secondRoundScores)) {
            if (player == secondPlayer) {
                secondScore = score;
                secondRoundScores = roundScores;
                return;
            }
            thirdScore = secondScore;
            thirdPlayer = secondPlayer;
            thirdRoundScores = secondRoundScores;
            secondScore = score;
            secondPlayer = player;
            secondRoundScores = roundScores;
        } else if (beats(score, roundScores, thirdScore, thirdRoundScores)) {
            if (player == thirdPlayer) {
                thirdScore = score;
                thirdRoundScores = roundScores;
                return;
            }
            thirdScore = score;
            thirdPlayer = player;
            thirdRoundScores = roundScores;
        }
    }

    public void update(Player player, int score) {
        update(player, score, null);
    }

    public String toString() {
        if (topPlayer == null) {
            return "No scores yet!";
        }
        if (secondPlayer == null) {
            return ":first_place_medal: <@" + topPlayer.name + ">: " + topScore;
        }
        if (thirdPlayer == null) {
            return ":first_place_medal: <@" + topPlayer.name + ">: " + topScore + "\n" + ":second_place_medal: <@"
                    + secondPlayer.name + ">: "
                    + secondScore;
        }
        return ":first_place_medal: <@" + topPlayer.name + ">: " + topScore + "\n" + ":second_place_medal: <@"
                + secondPlayer.name + ">: "
                + secondScore + "\n" + ":third_place_medal: <@" + thirdPlayer.name + ">: " + thirdScore;
    }
}
