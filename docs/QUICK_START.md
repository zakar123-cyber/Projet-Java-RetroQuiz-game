# 🚀 RetroQuiz Quiz Module - Quick Start Guide

## ⚡ Get Started in 5 Minutes!

---

## Step 1: Setup Database (2 minutes)

### Open SQL Server Management Studio

1. Connect to your SQL Server instance
2. Open `database_schema.sql`
3. Click **Execute** (or press F5)

✅ This creates:
- 5 tables (questions, players, game_sessions, session_players, player_answers)
- 15 sample questions
- 10 sample players

---

## Step 2: Test the Quiz (1 minute)

### Run the Test Launcher

**In IntelliJ IDEA or your IDE:**

1. Open `QuizTestLauncher.java`
2. Right-click → **Run 'QuizTestLauncher.main()'**

**OR via Maven:**

```bash
mvn javafx:run -Djavafx.mainClass=com.example.retroquiz.QuizTestLauncher
```

✅ You should see:
- Quiz window opens
- Question displayed
- Timer counting down
- 4 answer buttons

### Try It Out:
- Click an answer button
- Watch the visual feedback (green/red)
- See your score update
- Complete all 10 questions
- View results screen

---

## Step 3: Integrate with Your Lobby (2 minutes)

### In Your Lobby Controller

Add this code where you want to start the quiz:

```java
@FXML
private void handleStartQuiz() {
    try {
        // 1. Convert your lobby users to Player objects
        PlayerService playerService = new PlayerService();
        List<Player> players = new ArrayList<>();
        
        for (User user : lobbyUsers) {
            Player player = playerService.getPlayerByUsername(user.getUsername());
            if (player == null) {
                player = playerService.createPlayer(user.getUsername());
            }
            players.add(player);
        }
        
        // 2. Load quiz scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz-game.fxml"));
        Scene scene = new Scene(loader.load());
        
        // 3. Configure quiz
        QuizController quizController = loader.getController();
        quizController.setPlayers(players);
        quizController.startQuiz(10); // 10 questions
        
        // 4. Show quiz
        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.setScene(scene);
        
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

✅ That's it! Your lobby now launches the quiz.

---

## 🎯 What You Get

### Quiz Features:
- ✅ 15-second countdown timer
- ✅ Visual feedback (green = correct, red = wrong)
- ✅ Real-time scoring (+10, -5, -15 points)
- ✅ Automatic progression
- ✅ Results screen with rankings
- ✅ Global leaderboard

### Files Created:
- **8 Java files** (models, services, controllers)
- **3 FXML files** (quiz, results, leaderboard)
- **1 SQL schema** (database setup)
- **4 Documentation files** (guides and diagrams)

---

## 📚 Documentation

### Quick Reference:
- **IMPLEMENTATION_SUMMARY.md** - What was built
- **QUIZ_MODULE_README.md** - Complete documentation
- **QUIZ_INTEGRATION_GUIDE.md** - Detailed integration
- **ARCHITECTURE_DIAGRAM.md** - Visual diagrams

### Key Methods:
```java
// Set players (REQUIRED)
quizController.setPlayers(List<Player> players);

// Set session ID (optional)
quizController.setSessionId(int sessionId);

// Start quiz (REQUIRED)
quizController.startQuiz(int questionCount);
```

---

## ⚙️ Configuration

### Change Timer Duration:
In `QuizController.java`, line 41:
```java
private static final int TIMER_DURATION = 15; // Change to 10 or 20
```

### Change Scoring:
In `QuizController.java`, lines 44-46:
```java
private static final int POINTS_CORRECT = 10;
private static final int POINTS_WRONG = -5;
private static final int POINTS_NO_ANSWER = -15;
```

### Change Number of Questions:
```java
quizController.startQuiz(15); // Change from 10 to any number
```

---

## 🐛 Troubleshooting

### Quiz doesn't start?
✅ Make sure you called `setPlayers()` before `startQuiz()`

### No questions loaded?
✅ Run `database_schema.sql` to insert sample questions

### Database connection failed?
✅ Check `DatabaseConnection.java` settings
✅ Verify SQL Server is running
✅ Ensure DLL is installed (see main README.md)

### Timer not working?
✅ Check console for errors
✅ Verify JavaFX Timeline is supported

---

## 🎨 Customization

### Add More Questions:
```sql
INSERT INTO questions (question, optionA, optionB, optionC, optionD, correct_option, category)
VALUES ('Your question?', 'Option A', 'Option B', 'Option C', 'Option D', 'A', 'Category');
```

### Load Questions by Category:
```java
// In QuizService.java
List<Question> questions = quizService.loadQuestionsByCategory("Technology", 10);
```

### Change Colors:
Edit `retro-styles.css`:
- Cyan: `#00ffff`
- Yellow: `#ffff00`
- Magenta: `#ff00ff`

---

## 📊 Database Tables

After running `database_schema.sql`, you'll have:

| Table | Purpose |
|-------|---------|
| `questions` | Quiz questions (15 samples) |
| `players` | Player statistics (10 samples) |
| `game_sessions` | Game session tracking |
| `session_players` | Player participation |
| `player_answers` | Individual answer records |

---

## 🎮 Testing Checklist

- [ ] Database schema executed successfully
- [ ] QuizTestLauncher runs without errors
- [ ] Questions display correctly
- [ ] Timer counts down
- [ ] Answer buttons work
- [ ] Visual feedback shows (green/red)
- [ ] Score updates in real-time
- [ ] Results screen appears after quiz
- [ ] Leaderboard shows data

---

## 🚀 Next Steps

1. ✅ **Test with QuizTestLauncher** - Verify everything works
2. 📋 **Read QUIZ_INTEGRATION_GUIDE.md** - Understand integration
3. 🔧 **Customize timer/scoring** - Adjust to your needs
4. 🎮 **Integrate with lobby** - Connect to your existing code
5. 📚 **Add more questions** - Expand your question database

---

## 💡 Pro Tips

### Single Player Mode:
```java
Player player = playerService.getPlayerByUsername("TestUser");
quizController.setPlayers(List.of(player)); // Single player
```

### Multiplayer Mode:
```java
List<Player> players = new ArrayList<>();
// Add multiple players
quizController.setPlayers(players); // Multiplayer
```

### Track Sessions:
```java
// Create session in database first
quizController.setSessionId(sessionId);
// All answers will be recorded with this session ID
```

---

## 📞 Need Help?

1. **Check console output** for error messages
2. **Review QUIZ_INTEGRATION_GUIDE.md** for detailed examples
3. **Look for `// LOBBY INTEGRATION` comments** in code
4. **Check ARCHITECTURE_DIAGRAM.md** for visual flow

---

## ✅ Success Checklist

You're ready to integrate when:

- [x] Database schema is created
- [x] QuizTestLauncher runs successfully
- [x] You understand the Player model
- [x] You know how to call `setPlayers()` and `startQuiz()`
- [x] You've read the integration guide

---

**🎉 You're all set! Start building your quiz game!**

---

## Quick Command Reference

```bash
# Run test launcher
mvn javafx:run -Djavafx.mainClass=com.example.retroquiz.QuizTestLauncher

# Run main application
mvn javafx:run

# Clean and rebuild
mvn clean install

# Run with specific main class
mvn javafx:run -Djavafx.mainClass=com.example.retroquiz.HelloApplication
```

---

**Total Setup Time: ~5 minutes** ⚡

**Files to Review:**
1. `IMPLEMENTATION_SUMMARY.md` - Overview
2. `QUIZ_INTEGRATION_GUIDE.md` - Integration details
3. `QuizTestLauncher.java` - Example code

**Happy Coding! 🎮**
