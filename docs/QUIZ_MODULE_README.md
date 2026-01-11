# 🎮 RetroQuiz - Modular Quiz System

## 📋 Overview

A complete, modular JavaFX quiz gameplay system with:
- ✅ Real-time countdown timer
- ✅ Answer validation with visual feedback
- ✅ Dynamic scoring system
- ✅ Multiplayer support (lobby-ready)
- ✅ Global leaderboard
- ✅ Database persistence
- ✅ Retro neon aesthetic

---

## 🏗️ Architecture

### **MVC Pattern**

```
┌─────────────────────────────────────────┐
│              MODELS                     │
│  - Question.java                        │
│  - Player.java                          │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│            SERVICES                     │
│  - QuizService.java                     │
│  - PlayerService.java                   │
│  - DatabaseConnection.java              │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│           CONTROLLERS                   │
│  - QuizController.java                  │
│  - ResultController.java                │
│  - LeaderboardController.java           │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│              VIEWS                      │
│  - quiz-game.fxml                       │
│  - result-screen.fxml                   │
│  - leaderboard.fxml                     │
└─────────────────────────────────────────┘
```

---

## 📁 File Structure

```
src/main/
├── java/com/example/retroquiz/
│   ├── Backend/
│   │   ├── Question.java          ← Question model
│   │   ├── Player.java             ← Player model (NEW)
│   │   ├── QuizService.java        ← Quiz logic (NEW)
│   │   ├── PlayerService.java      ← Player management (NEW)
│   │   └── DatabaseConnection.java ← Existing DB connection
│   ├── QuizController.java         ← Quiz gameplay (NEW)
│   ├── ResultController.java       ← Results screen (NEW)
│   ├── LeaderboardController.java  ← Leaderboard (NEW)
│   └── QuizTestLauncher.java       ← Test launcher (NEW)
└── resources/
    ├── quiz-game.fxml              ← Quiz UI (NEW)
    ├── result-screen.fxml          ← Results UI (NEW)
    ├── leaderboard.fxml            ← Leaderboard UI (NEW)
    └── retro-styles.css            ← Existing styles

database_schema.sql                 ← Database setup (NEW)
QUIZ_INTEGRATION_GUIDE.md           ← Integration docs (NEW)
```

---

## 🚀 Quick Start

### **1. Setup Database**

Run the SQL schema to create tables and sample data:

```sql
-- In SQL Server Management Studio or equivalent
-- Execute: database_schema.sql
```

This creates:
- `questions` table (15 sample questions)
- `players` table (10 sample players)
- `game_sessions` table
- `session_players` table
- `player_answers` table

### **2. Test the Quiz Module**

Run the standalone test launcher:

```java
// Run QuizTestLauncher.java
public static void main(String[] args) {
    Application.launch(QuizTestLauncher.class, args);
}
```

This will:
1. Create a test player
2. Load 10 random questions
3. Start the quiz
4. Show results when complete

### **3. Integrate with Your Lobby**

See `QUIZ_INTEGRATION_GUIDE.md` for detailed integration instructions.

**Quick example:**

```java
// In your lobby controller
@FXML
private void handleStartQuiz() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz-game.fxml"));
    Scene scene = new Scene(loader.load());
    
    QuizController quizController = loader.getController();
    quizController.setPlayers(lobbyPlayers); // Your lobby players
    quizController.startQuiz(10);
    
    stage.setScene(scene);
}
```

---

## 🎯 Features

### **Quiz Gameplay**

- **Timer**: 15-second countdown (configurable)
- **Questions**: Loaded randomly from database
- **No Repeats**: Questions never repeat in same session
- **Visual Feedback**:
  - ✅ Correct answer → Green button
  - ❌ Wrong answer → Red button (correct shown in green)
  - ⏱️ Timeout → Correct answer highlighted

### **Scoring System**

| Event | Points |
|-------|--------|
| Correct answer | +10 |
| Wrong answer | -5 |
| No answer (timeout) | -15 |

### **Multiplayer Support**

- Accepts `List<Player>` for multiple players
- Calculates rankings based on score
- Tiebreaker: Earlier answer time wins
- Records all answers to database

### **Leaderboard**

- Shows top 10 players globally
- Displays:
  - Rank
  - Username
  - Total points
  - Games played
  - Accuracy percentage
- Visual highlighting for top 3

---

## ⚙️ Configuration

### **Timer Duration**

In `QuizController.java`:

```java
private static final int TIMER_DURATION = 15; // seconds
```

Change to `10` or `20` as needed.

### **Scoring Rules**

In `QuizController.java`:

```java
private static final int POINTS_CORRECT = 10;
private static final int POINTS_WRONG = -5;
private static final int POINTS_NO_ANSWER = -15;
```

### **Number of Questions**

When starting quiz:

```java
quizController.startQuiz(15); // Change from 10 to any number
```

### **Question Categories**

Load questions from specific category:

```java
// In QuizService.java
List<Question> questions = quizService.loadQuestionsByCategory("Technology", 10);
```

---

## 🔌 Lobby Integration

### **Required Steps**

1. **Convert lobby users to Player objects**
2. **Call `setPlayers(List<Player>)`**
3. **Call `startQuiz(int questionCount)`**
4. **Switch to quiz scene**

### **Example Code**

```java
// 1. Get players from lobby
List<User> lobbyUsers = getLobbyUsers();

// 2. Convert to Player objects
PlayerService playerService = new PlayerService();
List<Player> players = new ArrayList<>();

for (User user : lobbyUsers) {
    Player player = playerService.getPlayerByUsername(user.getUsername());
    if (player == null) {
        player = playerService.createPlayer(user.getUsername());
    }
    players.add(player);
}

// 3. Load quiz
FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz-game.fxml"));
Scene scene = new Scene(loader.load());
QuizController quizController = loader.getController();

// 4. Configure and start
quizController.setPlayers(players);
quizController.setSessionId(sessionId); // Optional
quizController.startQuiz(10);

// 5. Show quiz
stage.setScene(scene);
```

See `QUIZ_INTEGRATION_GUIDE.md` for complete details.

---

## 📊 Database Schema

### **questions**
```sql
id, question, optionA, optionB, optionC, optionD, 
correct_option, difficulty, category, created_at
```

### **players**
```sql
id, username, total_points, games_played, 
correct_answers, wrong_answers, no_answers, 
created_at, last_played
```

### **game_sessions**
```sql
id, session_code, host_id, status, 
created_at, started_at, ended_at
```

### **session_players**
```sql
id, session_id, player_id, score, rank, joined_at
```

### **player_answers**
```sql
id, session_id, player_id, question_id, 
selected_option, is_correct, time_taken, 
points_earned, answered_at
```

---

## 🎨 UI/UX

### **Retro Neon Theme**

- **Colors**:
  - Cyan (#00ffff) - Default/Guest
  - Yellow (#ffff00) - Login/Warning
  - Magenta (#ff00ff) - Special/Exit
  - Green (#00ff00) - Correct
  - Red (#ff0000) - Wrong

- **Fonts**: Impact (retro style)
- **Effects**: Neon glow, drop shadows
- **Background**: Animated GIF (retro-neon.gif)

### **Responsive Design**

- Timer changes color based on time remaining
- Progress bar shows time visually
- Buttons disabled after answer
- Smooth transitions between questions

---

## 🧪 Testing

### **Test Quiz Gameplay**

```bash
# Run QuizTestLauncher
mvn javafx:run -Djavafx.mainClass=com.example.retroquiz.QuizTestLauncher
```

### **Test Leaderboard**

```java
// In QuizTestLauncher.java, modify start() method:
testLeaderboard(primaryStage);
```

### **Test Results Screen**

```java
// In QuizTestLauncher.java, modify start() method:
testResults(primaryStage);
```

---

## 🐛 Troubleshooting

### **No questions loaded**
- ✅ Run `database_schema.sql`
- ✅ Check database connection
- ✅ Verify `questions` table has data

### **Timer not working**
- ✅ Ensure JavaFX Timeline is supported
- ✅ Check for console errors
- ✅ Verify JavaFX version compatibility

### **Players not showing in results**
- ✅ Call `setPlayers()` before `startQuiz()`
- ✅ Ensure Player objects have valid data
- ✅ Check console for errors

### **Database connection failed**
- ✅ Verify SQL Server is running
- ✅ Check `DatabaseConnection.java` settings
- ✅ Ensure `mssql-jdbc_auth` DLL is installed

---

## 📝 Code Quality

### **Clean Code Principles**

- ✅ No hard-coded players
- ✅ No static global state
- ✅ Clear separation of concerns
- ✅ Extensive comments
- ✅ LOBBY INTEGRATION markers

### **Design Patterns**

- **MVC**: Separation of model, view, controller
- **Service Layer**: Business logic in services
- **DAO Pattern**: Database access abstraction
- **Observer**: JavaFX properties for UI updates

---

## 🚀 Next Steps

1. **Run database schema** ✅
2. **Test with QuizTestLauncher** ✅
3. **Integrate with your lobby** 📋
4. **Customize timer/scoring** ⚙️
5. **Add more questions** 📚
6. **Implement real-time multiplayer** 🌐

---

## 📚 Documentation

- **Integration Guide**: `QUIZ_INTEGRATION_GUIDE.md`
- **Database Schema**: `database_schema.sql`
- **Code Comments**: Look for `// LOBBY INTEGRATION` markers

---

## 🎓 Learning Resources

### **Key Classes to Understand**

1. **QuizController.java** - Main quiz logic
2. **QuizService.java** - Question loading
3. **PlayerService.java** - Player management
4. **Question.java** - Question model
5. **Player.java** - Player model

### **Integration Points**

- `setPlayers(List<Player>)` - Inject player data
- `setSessionId(int)` - Track game session
- `startQuiz(int)` - Begin quiz with N questions

---

## ✅ Checklist

Before integrating with lobby:

- [ ] Database schema created
- [ ] Sample questions inserted
- [ ] QuizTestLauncher runs successfully
- [ ] Understand Player model
- [ ] Read QUIZ_INTEGRATION_GUIDE.md
- [ ] Test single-player mode
- [ ] Plan multiplayer synchronization

---

## 🤝 Support

**Issues?**
1. Check console output for errors
2. Review `QUIZ_INTEGRATION_GUIDE.md`
3. Look for `// LOBBY INTEGRATION` comments in code
4. Verify database connection

---

**Built with ❤️ for RetroQuiz**
