# 🎮 RetroQuiz - Simple 4-Player Setup (No Database!)

## ✅ **Works on ANY Computer - Zero Configuration!**

This guide shows you how to run a 4-player quiz game **without any database setup**.

---

## 🚀 **Setup Instructions**

### **Step 1: Copy Project to All Computers**

1. Copy the entire project folder to a USB drive
2. Copy to each of the 4 computers
3. That's it!

---

### **Step 2: Run on Each Computer**

On **each computer**:

1. Open project in IntelliJ
2. Wait for Maven to download dependencies (~1 minute)
3. Open `QuizTestLauncher.java`
4. Right-click → **Run 'QuizTestLauncher.main()'**

---

### **Step 3: Play the Quiz!**

Each player will:
- ✅ See the same 20 questions (in random order)
- ✅ Answer with timer (15 seconds per question)
- ✅ Get scored automatically (+10 correct, -5 wrong, -15 timeout)
- ✅ See final score at the end

---

## 🏆 **Determining the Winner**

After everyone finishes:

1. **Each player screenshots** their final score
2. **Compare scores**
3. **Highest score wins!**

---

## 📊 **What Each Player Sees**

### **During Quiz:**
```
Question 1 / 20
Score: 0
Time: 15s

[Question text here]

A: Option A
B: Option B  
C: Option C
D: Option D
```

### **After Quiz:**
```
QUIZ COMPLETE!

Your Score: 150 points

Rank: 1st
- TestPlayer: 150 points

[Play Again] [Leaderboard] [Main Menu]
```

---

## 🎯 **Questions Included**

The quiz has **20 hardcoded questions** covering:
- 💻 Technology
- 🖥️ Hardware  
- 🌐 Internet
- 💾 Programming
- 🔢 Computer Science
- 📜 History

**Questions are shuffled** each time, so order varies!

---

## ⚙️ **Customization (Optional)**

### **Change Number of Questions:**

In `QuizTestLauncher.java`, line 74:
```java
quizController.startQuiz(10); // Change to 5, 10, 15, or 20
```

### **Change Timer Duration:**

In `QuizController.java`, line 75:
```java
private static final int TIMER_DURATION = 15; // Change to 10, 15, or 20 seconds
```

### **Change Scoring:**

In `QuizController.java`, lines 76-78:
```java
private static final int POINTS_CORRECT = 10;  // Points for correct answer
private static final int POINTS_WRONG = -5;    // Points for wrong answer  
private static final int POINTS_TIMEOUT = -15; // Points for no answer
```

---

## 🎮 **Game Modes**

### **Mode 1: Independent Play (Recommended)**
- Each player plays on their own computer
- Everyone answers at their own pace
- Compare final scores

### **Mode 2: Synchronized Play**
- All players start at the same time
- Use a timer app to keep everyone in sync
- More competitive!

### **Mode 3: Same Computer (Turn-Based)**
- Players take turns on one computer
- Each player gets their own session
- Good for demos

---

## 📝 **Sample Score Sheet**

```
RetroQuiz Tournament - [Date]

Player 1: _____ points
Player 2: _____ points
Player 3: _____ points
Player 4: _____ points

Winner: _____________
```

---

## 🐛 **Troubleshooting**

### **"No questions loaded"**
→ This shouldn't happen with hardcoded questions
→ Make sure you're running the latest version

### **Quiz window doesn't open**
→ Check console for errors
→ Make sure JavaFX is installed (should auto-download via Maven)

### **Buttons don't work**
→ This was fixed - make sure you have the latest code

---

## ✨ **Advantages of This Approach**

✅ **Zero setup time** - Just copy and run  
✅ **No internet needed** - Works offline  
✅ **No database issues** - Everything is hardcoded  
✅ **Works on any computer** - Just needs Java  
✅ **Perfect for demos** - Reliable and fast  
✅ **Easy to share** - Copy folder and go  

---

## 🎯 **For Your Presentation/Demo**

1. **Setup:** 2 minutes (copy project)
2. **Run:** 30 seconds per computer
3. **Play:** 5-10 minutes (20 questions × 15 seconds)
4. **Results:** Instant

**Total time: ~15 minutes for complete 4-player game!**

---

## 🚀 **Next Steps**

1. ✅ Test on your computer (run `QuizTestLauncher`)
2. ✅ Copy to other 3 computers
3. ✅ Run on each computer
4. ✅ Play and compare scores!

---

## 💡 **Future Enhancements**

Later, you can add:
- Cloud database (when network issues are resolved)
- Real-time multiplayer synchronization
- Online leaderboard
- Session codes for joining games

But for now, **this works perfectly for 4 players!** 🎉

---

**Ready to play? Just run `QuizTestLauncher` and enjoy! 🎮**
