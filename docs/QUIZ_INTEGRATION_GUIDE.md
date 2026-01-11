# 🎮 RetroQuiz - Quiz Module Integration Guide

## 📋 Overview

This document explains how to integrate the quiz gameplay module with your existing lobby system.

---

## 🔌 LOBBY INTEGRATION POINTS

### **1. Starting a Quiz from the Lobby**

When players are ready in the lobby, use this code to launch the quiz:

```java
// In your QuizLobbyController.java or similar

@FXML
private void handleStartQuiz() {
    try {
        // Load the quiz game scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz-game.fxml"));
        Scene scene = new Scene(loader.load());
        
        // Get the quiz controller
        QuizController quizController = loader.getController();
        
        // STEP 1: Prepare player list
        List<Player> players = new ArrayList<>();
        
        // Example: Convert your lobby users to Player objects
        for (User user : lobbyUsers) {
            // Option A: Load existing player from database
            Player player = playerService.getPlayerByUsername(user.getUsername());
            
            // Option B: Create new player if doesn't exist
            if (player == null) {
                player = playerService.createPlayer(user.getUsername());
            }
            
            players.add(player);
        }
        
        // STEP 2: Set players in quiz controller
        quizController.setPlayers(players);
        
        // STEP 3: (Optional) Set session ID for database tracking
        quizController.setSessionId(currentSessionId);
        
        // STEP 4: Start the quiz with desired number of questions
        quizController.startQuiz(10); // 10 questions
        
        // STEP 5: Switch to quiz scene
        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("RetroQuiz - Game");
        
    } catch (IOException e) {
        System.err.println("Failed to start quiz: " + e.getMessage());
        e.printStackTrace();
    }
}
```

---

### **2. Player Data Flow**

```
┌─────────────┐
│   LOBBY     │
│  (Users)    │
└──────┬──────┘
       │
       │ Convert User → Player
       ▼
┌─────────────┐
│    QUIZ     │
│  (Players)  │
└──────┬──────┘
       │
       │ Calculate scores
       ▼
┌─────────────┐
│   RESULTS   │
│  (Rankings) │
└──────┬──────┘
       │
       │ Update database
       ▼
┌─────────────┐
│ LEADERBOARD │
│ (All-time)  │
└─────────────┘
```

---

### **3. Converting Existing User to Player**

If you already have a `User` class in your lobby:

```java
// In your lobby code
User lobbyUser = getCurrentUser(); // Your existing User object

// Convert to Player for quiz
PlayerService playerService = new PlayerService();
Player quizPlayer = playerService.getPlayerByUsername(lobbyUser.getUsername());

if (quizPlayer == null) {
    // First time playing - create new player record
    quizPlayer = playerService.createPlayer(lobbyUser.getUsername());
}

// Now pass to quiz
List<Player> players = List.of(quizPlayer);
quizController.setPlayers(players);
```

---

### **4. Multiplayer Session Management**

For multiplayer quizzes, create a game session first:

```java
// Create game session in database
String sql = "INSERT INTO game_sessions (session_code, host_id, status) VALUES (?, ?, 'ACTIVE')";
// ... execute SQL and get session_id

// Then pass session ID to quiz
quizController.setSessionId(sessionId);

// All player answers will be recorded with this session_id
```

---

### **5. Receiving Results from Quiz**

The quiz automatically navigates to the results screen. To get results back in your lobby:

**Option A: Modify ResultController**

```java
// In ResultController.java, add a callback:
private Consumer<List<Player>> onResultsComplete;

public void setOnResultsComplete(Consumer<List<Player>> callback) {
    this.onResultsComplete = callback;
}

// Then in your lobby:
resultController.setOnResultsComplete(rankedPlayers -> {
    // Handle results in lobby
    updateLobbyWithResults(rankedPlayers);
});
```

**Option B: Query database after quiz**

```java
// In your lobby, after quiz ends:
String sql = "SELECT * FROM session_players WHERE session_id = ? ORDER BY score DESC";
// ... load results from database
```

---

## 🎯 CONFIGURATION OPTIONS

### **Timer Duration**

In `QuizController.java`, line 41:
```java
private static final int TIMER_DURATION = 15; // Change to 10 or 20 seconds
```

### **Scoring Rules**

In `QuizController.java`, lines 44-46:
```java
private static final int POINTS_CORRECT = 10;    // Change correct answer points
private static final int POINTS_WRONG = -5;      // Change wrong answer penalty
private static final int POINTS_NO_ANSWER = -15; // Change timeout penalty
```

### **Number of Questions**

When starting quiz:
```java
quizController.startQuiz(15); // Change from 10 to any number
```

### **Question Categories**

To load questions from a specific category:
```java
// In QuizService.java, use:
List<Question> questions = quizService.loadQuestionsByCategory("Technology", 10);
```

---

## 📊 DATABASE INTEGRATION

### **Required Tables**

Make sure these tables exist in your SQL Server database:

1. **questions** - Quiz questions
2. **players** - Player statistics
3. **game_sessions** - Game session tracking
4. **session_players** - Player participation in sessions
5. **player_answers** - Individual answer records

Run the `database_schema.sql` file to create all tables.

---

## 🔄 WORKFLOW EXAMPLE

### **Complete Single-Player Flow**

```java
// 1. User logs in (your existing code)
User user = userService.loginUser(username, password);

// 2. User clicks "Play Quiz" in lobby
PlayerService playerService = new PlayerService();
Player player = playerService.getPlayerByUsername(user.getUsername());

if (player == null) {
    player = playerService.createPlayer(user.getUsername());
}

// 3. Load quiz
FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz-game.fxml"));
Scene scene = new Scene(loader.load());
QuizController quizController = loader.getController();

// 4. Configure quiz
quizController.setPlayers(List.of(player));
quizController.startQuiz(10);

// 5. Show quiz
stage.setScene(scene);

// 6. Quiz automatically shows results when done
// 7. Results screen updates database
// 8. Player can view leaderboard or return to lobby
```

### **Complete Multiplayer Flow**

```java
// 1. Host creates lobby with tournament code
TournamentService tournamentService = new TournamentService();
String code = tournamentService.getActiveTournamentCode();

// 2. Players join lobby using code
List<User> lobbyUsers = new ArrayList<>();
// ... users join ...

// 3. Host clicks "Start Quiz"
PlayerService playerService = new PlayerService();
List<Player> players = new ArrayList<>();

for (User user : lobbyUsers) {
    Player player = playerService.getPlayerByUsername(user.getUsername());
    if (player == null) {
        player = playerService.createPlayer(user.getUsername());
    }
    players.add(player);
}

// 4. Create game session
// ... insert into game_sessions table, get sessionId ...

// 5. Start quiz
quizController.setPlayers(players);
quizController.setSessionId(sessionId);
quizController.startQuiz(10);

// 6. All players see same questions (in multiplayer, you'd sync this)
// 7. Results show rankings
// 8. Update session status to 'COMPLETED'
```

---

## 🎨 CUSTOMIZATION

### **Retro Theme Colors**

The quiz uses your existing `retro-styles.css`. To customize:

- **Cyan** (#00ffff) - Guest/default actions
- **Yellow** (#ffff00) - Login/warning actions  
- **Magenta** (#ff00ff) - Register/special actions

### **Background**

The quiz uses the same animated GIF background from your lobby:
- File: `/retro-neon.gif`
- Opacity: 0.3 (adjustable in FXML)

---

## 🐛 TROUBLESHOOTING

### **"No players provided to quiz"**
- Make sure to call `setPlayers()` before `startQuiz()`

### **"No questions loaded from database"**
- Run `database_schema.sql` to insert sample questions
- Check database connection in `DatabaseConnection.java`

### **Timer not working**
- Ensure JavaFX Timeline is supported in your environment
- Check console for errors

### **Results screen not showing**
- Verify `/result-screen.fxml` is in resources folder
- Check that Player objects have valid data

---

## 📝 SUMMARY

### **Key Methods to Call from Lobby:**

1. `quizController.setPlayers(List<Player>)` - **REQUIRED**
2. `quizController.setSessionId(int)` - Optional (for database tracking)
3. `quizController.startQuiz(int)` - **REQUIRED**

### **Data You Need to Provide:**

- List of `Player` objects (minimum 1)
- Session ID (optional, for multiplayer tracking)
- Number of questions (default: 10)

### **What the Quiz Returns:**

- Ranked players (via ResultController)
- Updated player statistics (in database)
- Session results (in database)

---

## 🚀 NEXT STEPS

1. **Run database_schema.sql** to create tables and sample data
2. **Test single-player mode** first
3. **Integrate with your lobby** using the examples above
4. **Customize timer and scoring** as needed
5. **Add multiplayer synchronization** if needed

---

**Questions?** Check the code comments marked with `// LOBBY INTEGRATION`
