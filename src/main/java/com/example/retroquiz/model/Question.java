package com.example.retroquiz.model;

/**
 * Model class representing a quiz question.
 * 
 * This class is immutable and thread-safe for use in multiplayer scenarios.
 */
public class Question {
    private final int id;
    private final String question;
    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String optionD;
    private final char correctOption; // 'A', 'B', 'C', or 'D'
    private final String category;
    private final String difficulty;

    /**
     * Full constructor for Question.
     * 
     * @param id            Unique question identifier from database
     * @param question      The question text
     * @param optionA       First answer option
     * @param optionB       Second answer option
     * @param optionC       Third answer option
     * @param optionD       Fourth answer option
     * @param correctOption Correct answer ('A', 'B', 'C', or 'D')
     * @param category      Question category (optional)
     * @param difficulty    Question difficulty (optional)
     */
    public Question(int id, String question, String optionA, String optionB,
            String optionC, String optionD, char correctOption,
            String category, String difficulty) {
        this.id = id;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = Character.toUpperCase(correctOption);
        this.category = category;
        this.difficulty = difficulty;
    }

    /**
     * Simplified constructor without category and difficulty.
     */
    public Question(int id, String question, String optionA, String optionB,
            String optionC, String optionD, char correctOption) {
        this(id, question, optionA, optionB, optionC, optionD, correctOption, null, "MEDIUM");
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public char getCorrectOption() {
        return correctOption;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    /**
     * Get the text of a specific option.
     * 
     * @param option The option letter ('A', 'B', 'C', or 'D')
     * @return The option text, or null if invalid option
     */
    public String getOptionText(char option) {
        return switch (Character.toUpperCase(option)) {
            case 'A' -> optionA;
            case 'B' -> optionB;
            case 'C' -> optionC;
            case 'D' -> optionD;
            default -> null;
        };
    }

    /**
     * Check if a given answer is correct.
     * 
     * @param selectedOption The option selected by the player
     * @return true if correct, false otherwise
     */
    public boolean isCorrect(char selectedOption) {
        return Character.toUpperCase(selectedOption) == correctOption;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", correctOption=" + correctOption +
                ", category='" + category + '\'' +
                '}';
    }
}
