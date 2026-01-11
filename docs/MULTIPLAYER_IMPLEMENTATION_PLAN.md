# 🎮 RetroQuiz - Complete Multiplayer Implementation Plan

## 📋 **Project Vision**

A multiplayer quiz game where:
- **Admin** creates quiz sessions with unique codes
- **Players** join from different locations (different WiFi networks)
- **Questions** stored in cloud database
- **Scores** automatically saved and ranked
- **Leaderboard** shows all-time top players

---

## 🏗️ **Complete Architecture**

```
┌─────────────────────────────────────────────────────────────┐
│                    CLOUD DATABASE                            │
│                  (Supabase PostgreSQL)                       │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ Tables:                                               │  │
│  │ - questions (quiz questions)                          │  │
│  │ - players (user accounts)                             │  │
│  │ - game_sessions (active/past games)                   │  │
│  │ - session_players (who's in each game)                │  │
│  │ - player_answers (all answers & scores)               │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            ↑
                            │ Internet Connection
                            ↓
┌──────────────────────────────────────────────────────────────┐
│                    ALL PLAYER COMPUTERS                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │   ADMIN PC  │  │  Player 1   │  │  Player 2   │  ...     │
│  │ (Host Game) │  │ (Join Code) │  │ (Join Code) │          │
│  └─────────────┘  └─────────────┘  └─────────────┘          │
└──────────────────────────────────────────────────────────────┘
```

---

## 📅 **Implementation Timeline**

### **Phase 1: Cloud Database Setup** (Day 1 - 2 hours)
### **Phase 2: Database Migration** (Day 1 - 1 hour)
### **Phase 3: Session Management** (Day 2-3 - 8 hours)
### **Phase 4: Multiplayer Quiz Logic** (Day 4-5 - 12 hours)
### **Phase 5: Testing & Polish** (Day 6-7 - 8 hours)

**Total Time: 1 Week (31 hours)**

---

# 📆 **PHASE 1: Cloud Database Setup**

## **Objective:** Setup free cloud PostgreSQL database

### **Step 1.1: Create Supabase Account** (10 min)
1. Go to `supabase.com`
2. Click "Start your project"
3. Sign up with GitHub/Google
4. Create new project:
   - Name: `RetroQuiz`
   - Database Password: (choose strong password)
   - Region: Closest to you
   - Click "Create new project"

### **Step 1.2: Get Connection Details** (5 min)
1. Go to **Settings → Database**
2. Copy these values:
   - **Host**: `db.xxxxx.supabase.co`
   - **Database**: `postgres`
   - **Port**: `5432`
   - **User**: `postgres`
   - **Password**: (your password)

### **Step 1.3: Update pom.xml** (5 min)

Add PostgreSQL driver:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.1</version>
</dependency>
```

### **Step 1.4: Create New DatabaseConnection Class** (15 min)

Create `CloudDatabaseConnection.java`:

```java
package com.example.retroquiz.Backend;

import java.sql.*;

public class CloudDatabaseConnection {
    
    private static final String DB_URL = "jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "your_password_here";
    
    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            System.err.println("Cloud database connection failed: " + e.getMessage());
            return null;
        }
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
```

### **Step 1.5: Test Connection** (10 min)

Create test file to verify connection works.

---

# 📆 **PHASE 2: Database Migration**

## **Objective:** Convert SQL Server schema to PostgreSQL

### **Step 2.1: Create PostgreSQL Schema** (30 min)

Create `cloud_schema.sql`:

```sql
-- Questions table
CREATE TABLE IF NOT EXISTS questions (
    id SERIAL PRIMARY KEY,
    question VARCHAR(500) NOT NULL,
    optionA VARCHAR(200) NOT NULL,
    optionB VARCHAR(200) NOT NULL,
    optionC VARCHAR(200) NOT NULL,
    optionD VARCHAR(200) NOT NULL,
    correct_option CHAR(1) NOT NULL CHECK (correct_option IN ('A', 'B', 'C', 'D')),
    difficulty VARCHAR(20) DEFAULT 'MEDIUM',
    category VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Players table
CREATE TABLE IF NOT EXISTS players (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100),
    password_hash VARCHAR(255),
    total_points INTEGER DEFAULT 0,
    games_played INTEGER DEFAULT 0,
    correct_answers INTEGER DEFAULT 0,
    wrong_answers INTEGER DEFAULT 0,
    no_answers INTEGER DEFAULT 0,
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_played TIMESTAMP
);

-- Game sessions table
CREATE TABLE IF NOT EXISTS game_sessions (
    id SERIAL PRIMARY KEY,
    session_code VARCHAR(10) UNIQUE NOT NULL,
    host_id INTEGER REFERENCES players(id),
    status VARCHAR(20) DEFAULT 'WAITING',
    question_count INTEGER DEFAULT 10,
    timer_duration INTEGER DEFAULT 15,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    ended_at TIMESTAMP
);

-- Session players table
CREATE TABLE IF NOT EXISTS session_players (
    id SERIAL PRIMARY KEY,
    session_id INTEGER REFERENCES game_sessions(id),
    player_id INTEGER REFERENCES players(id),
    score INTEGER DEFAULT 0,
    rank INTEGER,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(session_id, player_id)
);

-- Player answers table
CREATE TABLE IF NOT EXISTS player_answers (
    id SERIAL PRIMARY KEY,
    session_id INTEGER REFERENCES game_sessions(id),
    player_id INTEGER REFERENCES players(id),
    question_id INTEGER REFERENCES questions(id),
    selected_option CHAR(1),
    is_correct BOOLEAN,
    time_taken INTEGER,
    points_earned INTEGER,
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample questions
INSERT INTO questions (question, optionA, optionB, optionC, optionD, correct_option, category, difficulty)
VALUES 
('What year was the first computer mouse invented?', '1964', '1970', '1980', '1990', 'A', 'Technology', 'EASY'),
('Which programming language was created by James Gosling?', 'Python', 'Java', 'C++', 'Ruby', 'B', 'Programming', 'MEDIUM'),
('What does CPU stand for?', 'Central Processing Unit', 'Computer Personal Unit', 'Central Program Utility', 'Computer Processing Utility', 'A', 'Hardware', 'EASY'),
('In what year was Microsoft founded?', '1975', '1980', '1985', '1990', 'A', 'History', 'MEDIUM'),
('What is the maximum value of a byte?', '128', '255', '256', '512', 'B', 'Computer Science', 'MEDIUM'),
('Which company developed the first graphical web browser?', 'Microsoft', 'Netscape', 'Apple', 'IBM', 'B', 'Internet', 'MEDIUM'),
('What does RAM stand for?', 'Random Access Memory', 'Read Access Memory', 'Rapid Access Memory', 'Real Access Memory', 'A', 'Hardware', 'EASY'),
('Who is known as the father of computers?', 'Alan Turing', 'Charles Babbage', 'Bill Gates', 'Steve Jobs', 'B', 'History', 'MEDIUM'),
('What is the binary equivalent of decimal 10?', '1010', '1100', '1001', '1111', 'A', 'Computer Science', 'HARD'),
('Which protocol is used for secure web browsing?', 'HTTP', 'FTP', 'HTTPS', 'SMTP', 'C', 'Networking', 'MEDIUM'),
('What does HTML stand for?', 'Hyper Text Markup Language', 'High Tech Modern Language', 'Home Tool Markup Language', 'Hyperlinks and Text Markup Language', 'A', 'Web Development', 'EASY'),
('In which year was the World Wide Web invented?', '1989', '1991', '1993', '1995', 'A', 'Internet', 'MEDIUM'),
('What is the name of the first electronic computer?', 'ENIAC', 'UNIVAC', 'EDVAC', 'MARK I', 'A', 'History', 'HARD'),
('Which company created the Java programming language?', 'Microsoft', 'Sun Microsystems', 'Oracle', 'IBM', 'B', 'Programming', 'MEDIUM'),
('What does SQL stand for?', 'Structured Query Language', 'Simple Query Language', 'Standard Query Language', 'System Query Language', 'A', 'Database', 'EASY');

-- Create admin user (password: admin123)
INSERT INTO players (username, email, password_hash, is_admin)
VALUES ('admin', 'admin@retroquiz.com', 'admin123', TRUE);
```

### **Step 2.2: Run Schema in Supabase** (10 min)

1. Go to Supabase Dashboard
2. Click **SQL Editor**
3. Paste `cloud_schema.sql`
4. Click **Run**

### **Step 2.3: Update All Services** (20 min)

Update `QuizService.java`, `PlayerService.java` to use `CloudDatabaseConnection` instead of `DatabaseConnection`.

---

# 📆 **PHASE 3: Session Management**

## **Objective:** Create/join game sessions with codes

### **Step 3.1: Create SessionService** (2 hours)

```java
package com.example.retroquiz.Backend;

import java.sql.*;
import java.util.Random;

public class SessionService {
    
    /**
     * Generate unique 6-character session code
     */
    public String generateSessionCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return code.toString();
    }
    
    /**
     * Create new game session
     */
    public int createSession(int hostId, int questionCount, int timerDuration) {
        String code = generateSessionCode();
        String sql = "INSERT INTO game_sessions (session_code, host_id, question_count, timer_duration, status) " +
                     "VALUES (?, ?, ?, ?, 'WAITING') RETURNING id";
        
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null) return -1;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            stmt.setInt(2, hostId);
            stmt.setInt(3, questionCount);
            stmt.setInt(4, timerDuration);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int sessionId = rs.getInt(1);
                System.out.println("✅ Session created with code: " + code);
                return sessionId;
            }
        } catch (SQLException e) {
            System.err.println("Error creating session: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
        
        return -1;
    }
    
    /**
     * Join existing session with code
     */
    public int joinSession(String code, int playerId) {
        String sql = "SELECT id FROM game_sessions WHERE session_code = ? AND status = 'WAITING'";
        
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null) return -1;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int sessionId = rs.getInt("id");
                
                // Add player to session
                String insertSql = "INSERT INTO session_players (session_id, player_id) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, sessionId);
                    insertStmt.setInt(2, playerId);
                    insertStmt.executeUpdate();
                    
                    System.out.println("✅ Player joined session: " + code);
                    return sessionId;
                }
            } else {
                System.out.println("❌ Invalid or expired session code");
                return -1;
            }
        } catch (SQLException e) {
            System.err.println("Error joining session: " + e.getMessage());
            return -1;
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }
    
    /**
     * Start game session
     */
    public boolean startSession(int sessionId) {
        String sql = "UPDATE game_sessions SET status = 'ACTIVE', started_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null) return false;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error starting session: " + e.getMessage());
            return false;
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
    }
    
    /**
     * Get session status
     */
    public String getSessionStatus(int sessionId) {
        String sql = "SELECT status FROM game_sessions WHERE id = ?";
        
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null) return "UNKNOWN";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (SQLException e) {
            System.err.println("Error getting session status: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
        
        return "UNKNOWN";
    }
    
    /**
     * Get players in session
     */
    public List<Player> getSessionPlayers(int sessionId) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT p.* FROM players p " +
                     "JOIN session_players sp ON p.id = sp.player_id " +
                     "WHERE sp.session_id = ?";
        
        Connection conn = CloudDatabaseConnection.getConnection();
        if (conn == null) return players;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Player player = new Player(
                    rs.getInt("id"),
                    rs.getString("username")
                );
                players.add(player);
            }
        } catch (SQLException e) {
            System.err.println("Error getting session players: " + e.getMessage());
        } finally {
            CloudDatabaseConnection.closeConnection(conn);
        }
        
        return players;
    }
}
```

### **Step 3.2: Create HostGameController** (2 hours)

New controller for admin to create game sessions.

### **Step 3.3: Create JoinGameController** (2 hours)

New controller for players to join with code.

### **Step 3.4: Create WaitingLobbyController** (2 hours)

Shows players waiting for host to start game.

---

# 📆 **PHASE 4: Multiplayer Quiz Logic**

## **Objective:** Sync quiz across all players

### **Step 4.1: Question Synchronization** (3 hours)

When host starts quiz:
1. Load questions from database
2. Save question order to `game_sessions`
3. All players poll database for question order
4. Everyone gets same questions in same order

### **Step 4.2: Answer Collection** (3 hours)

Each player:
1. Answers question locally
2. Saves answer to `player_answers` table
3. Updates score in `session_players` table

### **Step 4.3: Progress Tracking** (2 hours)

Poll database to check:
- How many players finished current question
- When to move to next question
- When quiz is complete

### **Step 4.4: Results Aggregation** (2 hours)

After last question:
1. Calculate rankings from `session_players`
2. Show results to all players
3. Update `players` table with total points

### **Step 4.5: Update QuizController** (2 hours)

Modify existing `QuizController` to:
- Work in multiplayer mode
- Save answers to cloud database
- Check session status between questions

---

# 📆 **PHASE 5: Testing & Polish**

## **Objective:** Test and fix bugs

### **Step 5.1: Single PC Testing** (2 hours)
- Test create session
- Test join session
- Test quiz flow
- Test scoring

### **Step 5.2: Multi-PC Testing** (3 hours)
- Test on 2-4 different computers
- Test different WiFi networks
- Test simultaneous play
- Fix synchronization issues

### **Step 5.3: Edge Cases** (2 hours)
- Player disconnects mid-game
- Invalid session codes
- Duplicate usernames
- Network timeouts

### **Step 5.4: Polish UI** (1 hour)
- Add loading indicators
- Improve error messages
- Add success notifications

---

# 📊 **Complete File Structure**

```
RetroQuiz/
├── src/main/java/com/example/retroquiz/
│   ├── Backend/
│   │   ├── CloudDatabaseConnection.java ← NEW
│   │   ├── SessionService.java ← NEW
│   │   ├── QuizService.java ← UPDATED
│   │   ├── PlayerService.java ← UPDATED
│   │   ├── Question.java ← EXISTING
│   │   ├── Player.java ← EXISTING
│   │   └── QuestionBank.java ← EXISTING (fallback)
│   │
│   ├── Controllers/
│   │   ├── HostGameController.java ← NEW
│   │   ├── JoinGameController.java ← NEW
│   │   ├── WaitingLobbyController.java ← NEW
│   │   ├── QuizController.java ← UPDATED
│   │   ├── ResultController.java ← UPDATED
│   │   ├── LeaderboardController.java ← EXISTING
│   │   └── ... (existing controllers)
│   │
│   └── HelloApplication.java ← UPDATED
│
├── src/main/resources/
│   ├── host-game.fxml ← NEW
│   ├── join-game.fxml ← NEW
│   ├── waiting-lobby.fxml ← NEW
│   ├── quiz-game.fxml ← EXISTING
│   ├── result-screen.fxml ← EXISTING
│   └── ... (existing FXML files)
│
├── cloud_schema.sql ← NEW
├── pom.xml ← UPDATED (add PostgreSQL)
└── README.md ← UPDATED
```

---

# 🎯 **User Flow**

## **Admin (Host) Flow:**
```
1. Login as admin
2. Click "Host Game"
3. Select:
   - Number of questions (5, 10, 15)
   - Timer duration (10s, 15s, 20s)
4. Click "Create Session"
5. System shows code (e.g., "AB12CD")
6. Share code with players
7. Wait in lobby (see players joining)
8. Click "Start Quiz"
9. Play quiz
10. See final rankings
```

## **Player Flow:**
```
1. Open app
2. Click "Join Game"
3. Enter session code
4. Enter username
5. Wait in lobby
6. Quiz starts automatically
7. Answer questions
8. See final rankings
9. View leaderboard
```

---

# 💰 **Cost Breakdown**

| Service | Cost | Limits |
|---------|------|--------|
| **Supabase Database** | FREE | 500MB storage, 2GB bandwidth/month |
| **PostgreSQL** | FREE | Included in Supabase |
| **Hosting** | FREE | Cloud-based |
| **Total** | **$0/month** | Perfect for 4-10 players |

---

# ✅ **Success Criteria**

- [ ] 4 players can join from different WiFi networks
- [ ] All players see same questions
- [ ] Scores save automatically to cloud
- [ ] Leaderboard updates in real-time
- [ ] Admin can create unlimited sessions
- [ ] Players can join with simple code
- [ ] Works on any computer with internet

---

# 🚀 **Next Steps**

1. **Approve this plan** ✅
2. **I'll start Phase 1** (Cloud database setup)
3. **You test each phase** as I complete it
4. **We iterate** based on your feedback
5. **Launch!** 🎉

---

**Ready to start? Say "Let's do it!" and I'll begin with Phase 1! 🎮**
