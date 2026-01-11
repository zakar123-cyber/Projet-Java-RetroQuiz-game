# 🎮 RetroQuiz - NO DATABASE SETUP REQUIRED!

## ✅ **Works on ANY Computer - Zero Configuration!**

Your quiz module now works **WITHOUT any database setup**!

---

## 🚀 **How to Run (Super Simple)**

### **Step 1: Open in IntelliJ**
1. Open the project folder
2. Wait for Maven to download dependencies

### **Step 2: Run the Quiz**
1. Open `QuizTestLauncher.java`
2. Right-click → **Run 'QuizTestLauncher.main()'**
3. **That's it!** The quiz opens immediately!

---

## 🎯 **What You Get**

### **Offline Mode (No Database)**
- ✅ **15 hardcoded questions** (automatically loaded)
- ✅ **Full quiz gameplay** (timer, scoring, visual feedback)
- ✅ **Results screen** with rankings
- ✅ **Works on any computer** instantly
- ⚠️ Scores are NOT saved (temporary only)

### **Online Mode (With Database)**
- ✅ **Unlimited questions** from database
- ✅ **Persistent scores** and leaderboard
- ✅ **Game history** tracking
- ⚠️ Requires SQL Server setup

---

## 💡 **How It Works**

The quiz automatically detects if SQL Server is available:

```
Database Available? 
  ├─ YES → Loads questions from database ✅
  └─ NO  → Uses 15 hardcoded questions ✅
```

**You don't need to do anything!** It just works.

---

## 📊 **Hardcoded Questions**

The quiz includes 15 built-in questions covering:
- 💻 Technology
- 🖥️ Hardware
- 🌐 Internet
- 💾 Programming
- 🔢 Computer Science
- 📜 History

Questions are **automatically shuffled** each time!

---

## 🎓 **Perfect For:**

✅ **Demos** - Show the quiz without database setup  
✅ **Testing** - Test on any computer instantly  
✅ **Development** - Work offline without SQL Server  
✅ **Presentations** - No setup required for audience  
✅ **Portability** - Copy to USB and run anywhere  

---

## 🔧 **Optional: Enable Database Mode**

If you want to use the database (for persistent scores):

1. **Install SQL Server Express**
2. **Enable TCP/IP** on port 1433
3. **Run** `database_schema.sql` in SSMS
4. **Restart** the quiz

The quiz will **automatically** switch to database mode!

---

## 📁 **Files You Need**

To run on another computer, just copy:
```
Projet-Java-RetroQuiz-game-main/
├── src/
├── pom.xml
└── (that's it!)
```

Maven will download everything else automatically!

---

## 🎮 **Quick Test**

Run `QuizTestLauncher` and you should see:

```
⚠️ Database unavailable. Using hardcoded questions (OFFLINE MODE).
✅ Temporary player created: TestPlayer (Offline)
Quiz initialized with 1 player(s):
  - TestPlayer (Offline)
⚠️ Database unavailable. Using hardcoded questions (OFFLINE MODE).
Quiz started with 15 questions.
```

Then the quiz window opens with real questions!

---

## ✨ **Benefits**

| Feature | Offline Mode | Database Mode |
|---------|--------------|---------------|
| **Setup Time** | 0 seconds ⚡ | ~15 minutes |
| **Questions** | 15 hardcoded | Unlimited |
| **Portability** | ✅ Perfect | ❌ Needs SQL Server |
| **Scores Saved** | ❌ Temporary | ✅ Persistent |
| **Leaderboard** | ❌ No | ✅ Yes |
| **Works Anywhere** | ✅ Yes | ❌ Needs setup |

---

## 🎉 **You're Done!**

**No database setup needed!**  
**No configuration required!**  
**Just run and play!** 🎮

---

## 💬 **Console Messages Explained**

### **Offline Mode:**
```
⚠️ Database unavailable. Using hardcoded questions (OFFLINE MODE).
```
= Quiz is using 15 built-in questions (no database needed)

### **Online Mode:**
```
✅ Loaded 10 questions from database.
```
= Quiz loaded questions from SQL Server database

---

**Perfect for demos, testing, and development! 🚀**
