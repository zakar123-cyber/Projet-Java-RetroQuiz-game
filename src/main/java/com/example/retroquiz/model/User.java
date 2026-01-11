package com.example.retroquiz.model;

public class User {
    private final String username;
    private final boolean isGuest;
    private final String tournamentCode;
    // You can add score, profile pic path, etc., here later

    // Constructor for a registered user
    public User(String username, String tournamentCode) {
        this.username = username;
        this.isGuest = false;
        this.tournamentCode = tournamentCode;
    }

    // Constructor for a guest
    public User(String guestName) {
        this.username = guestName;
        this.isGuest = true;
        this.tournamentCode = null;
    }

    public String getUsername() {
        return username;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public String getTournamentCode() {
        return tournamentCode;
    }
}
