package com.example.retroquiz.model;

/**
 * Model class representing a player in the quiz system.
 * 
 * LOBBY INTEGRATION POINT:
 * This class will be populated by the lobby module and passed to the quiz
 * controller.
 * The lobby should create Player instances with at minimum: id and username.
 */
public class Player {
    private final int id;
    private final String username;
    private int currentScore; // Score in the current game session
    private int totalPoints; // Total points from all games (from database)
    private int gamesPlayed;
    private int correctAnswers;
    private int wrongAnswers;
    private int noAnswers;

    // Session-specific data
    private int rank; // Rank in current game (1st, 2nd, 3rd, etc.)
    private long lastAnswerTime; // Timestamp of last answer (for tiebreakers)

    /**
     * Full constructor (used when loading from database).
     * 
     * @param id             Player ID from database
     * @param username       Player username
     * @param totalPoints    Total points accumulated across all games
     * @param gamesPlayed    Number of games played
     * @param correctAnswers Total correct answers
     * @param wrongAnswers   Total wrong answers
     * @param noAnswers      Total unanswered questions
     */
    public Player(int id, String username, int totalPoints, int gamesPlayed,
            int correctAnswers, int wrongAnswers, int noAnswers) {
        this.id = id;
        this.username = username;
        this.totalPoints = totalPoints;
        this.gamesPlayed = gamesPlayed;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.noAnswers = noAnswers;
        this.currentScore = 0;
        this.rank = 0;
    }

    /**
     * Simplified constructor for new game sessions.
     * LOBBY INTEGRATION: Use this constructor when creating players from lobby
     * data.
     * 
     * @param id       Player ID
     * @param username Player username
     */
    public Player(int id, String username) {
        this(id, username, 0, 0, 0, 0, 0);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public int getNoAnswers() {
        return noAnswers;
    }

    public int getRank() {
        return rank;
    }

    public long getLastAnswerTime() {
        return lastAnswerTime;
    }

    // Setters for game session management
    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setLastAnswerTime(long lastAnswerTime) {
        this.lastAnswerTime = lastAnswerTime;
    }

    /**
     * Add points to the current game score.
     * 
     * @param points Points to add (can be negative for penalties)
     */
    public void addPoints(int points) {
        this.currentScore += points;
    }

    /**
     * Record a correct answer.
     */
    public void recordCorrectAnswer() {
        this.correctAnswers++;
    }

    /**
     * Record a wrong answer.
     */
    public void recordWrongAnswer() {
        this.wrongAnswers++;
    }

    /**
     * Record a missed/unanswered question.
     */
    public void recordNoAnswer() {
        this.noAnswers++;
    }

    /**
     * Add points to total points (for database persistence).
     * 
     * @param points Points to add to total
     */
    public void addToTotalPoints(int points) {
        this.totalPoints += points;
    }

    /**
     * Increment games played counter.
     */
    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    /**
     * Reset current game session data (for new game).
     */
    public void resetSession() {
        this.currentScore = 0;
        this.rank = 0;
        this.lastAnswerTime = 0;
    }

    /**
     * Get accuracy percentage.
     * 
     * @return Accuracy as a percentage (0-100)
     */
    public double getAccuracy() {
        int totalAnswered = correctAnswers + wrongAnswers;
        if (totalAnswered == 0)
            return 0.0;
        return (correctAnswers * 100.0) / totalAnswered;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", currentScore=" + currentScore +
                ", totalPoints=" + totalPoints +
                ", rank=" + rank +
                '}';
    }

    /**
     * Compare players by current score (for ranking).
     * Higher score = better rank.
     * If scores are equal, earlier answer time wins.
     */
    public int compareByScore(Player other) {
        if (this.currentScore != other.currentScore) {
            return Integer.compare(other.currentScore, this.currentScore); // Descending
        }
        // Tiebreaker: earlier answer time wins
        return Long.compare(this.lastAnswerTime, other.lastAnswerTime);
    }
}
