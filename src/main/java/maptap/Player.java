package maptap;

public class Player {
    public String name;
    public int totalScore;
    public int topScore;
    public int worstScore;
    public int gamesPlayed;
    public int averageScore;

    public Player(String name) {
        this.name = name;
        this.totalScore = 0;
        this.topScore = 0;
        this.worstScore = Integer.MAX_VALUE;
        this.gamesPlayed = 0;
        this.averageScore = 0;
    }

    public int addScore(int score) {
        this.totalScore += score;
        this.topScore = Math.max(this.topScore, score);
        this.worstScore = Math.min(this.worstScore, score);
        this.gamesPlayed++;
        this.averageScore = this.totalScore / this.gamesPlayed;
        return this.topScore;
    }

    public String getStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nTotal score: ").append(this.totalScore);
        sb.append("\n:gem: Best score: ").append(this.topScore);
        sb.append("\n:toilet: Worst score: ").append(this.worstScore);
        sb.append("\n:robot_face: Average score: ").append(this.averageScore).append("\n");
        return sb.toString();
    }
}
