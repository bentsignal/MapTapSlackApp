package maptap;

public class Message {
    protected String date;
    protected String name;
    protected int score;
    protected int[] guesses;

    public Message(String body, String name) {
        /*
         * Message in format:
         * www.maptap.gg April 24
         * 95:sports_medal: 88:tada: 94:sports_medal: 97:fire: 75:sun_with_face:
         * Final score: 887
         */
        this.name = name;
        // split on space or newline
        String[] parts = body.split(" |\\n");
        this.date = parts[1].toUpperCase() + " " + parts[2];
        this.guesses = new int[5];
        for (int i = 0; i < 5; i++) {
            String part = parts[3 + i];
            int colonIdx = part.indexOf(':');
            this.guesses[i] = Integer.parseInt(colonIdx == -1 ? part : part.substring(0, colonIdx));
        }
        this.score = Integer.parseInt(parts[10]);
    }
}
