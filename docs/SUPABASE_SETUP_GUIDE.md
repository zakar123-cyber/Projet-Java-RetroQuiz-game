# 🚀 Supabase Connection Setup Guide

## ✅ **What You've Done So Far:**
1. ✅ Created Supabase account
2. ✅ Created database tables (20 questions, 5 players)
3. ✅ Added PostgreSQL driver to pom.xml

---

## 📋 **Next Steps:**

### **Step 1: Get Your Supabase Connection String**

1. In Supabase dashboard, click **Settings** (⚙️ icon, left sidebar)
2. Click **Database**
3. Scroll down to **Connection string** section
4. Click **URI** tab
5. Copy the entire string (looks like this):
   ```
   postgresql://postgres.xxxxx:[YOUR-PASSWORD]@aws-0-us-east-1.pooler.supabase.com:6543/postgres
   ```
6. **IMPORTANT:** Replace `[YOUR-PASSWORD]` with your actual database password!

---

### **Step 2: Update CloudDatabaseConnection.java**

1. Open `CloudDatabaseConnection.java`
2. Find lines 22-24:
   ```java
   private static final String DB_URL = "jdbc:postgresql://YOUR_SUPABASE_HOST:5432/postgres";
   private static final String DB_USER = "postgres";
   private static final String DB_PASSWORD = "YOUR_SUPABASE_PASSWORD";
   ```

3. **Update DB_URL:**
   - Take your Supabase connection string
   - Add `jdbc:` at the beginning
   - Example:
     ```
     From: postgresql://postgres.xxxxx:password@aws-0-us-east-1.pooler.supabase.com:6543/postgres
     To:   jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:6543/postgres
     ```

4. **Update DB_PASSWORD:**
   - Replace `YOUR_SUPABASE_PASSWORD` with your actual password

**Example:**
```java
private static final String DB_URL = "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:6543/postgres";
private static final String DB_USER = "postgres";
private static final String DB_PASSWORD = "your_actual_password_here";
```

---

### **Step 3: Reload Maven Dependencies**

In IntelliJ:
1. Right-click on `pom.xml`
2. Click **Maven → Reload Project**
3. Wait for dependencies to download (~30 seconds)

---

### **Step 4: Test the Connection**

1. Open `CloudDatabaseConnection.java`
2. Right-click anywhere in the file
3. Click **Run 'CloudDatabaseConnection.main()'**

**You should see:**
```
Testing Supabase connection...
✅ Connected to Supabase cloud database!
✅ Connection successful!
✅ Found 20 questions in database
✅ Database connection closed.
```

**If you see errors:**
- ❌ Check DB_URL is correct
- ❌ Check DB_PASSWORD is correct
- ❌ Check internet connection
- ❌ Make sure Maven reloaded

---

### **Step 5: Update QuizService to Use Cloud Database**

Once connection test works, I'll help you update:
- `QuizService.java` → Use `CloudDatabaseConnection` instead of `DatabaseConnection`
- `PlayerService.java` → Use `CloudDatabaseConnection`
- `QuizTestLauncher.java` → Test with cloud database

---

## 🎯 **What Happens Next:**

After successful connection:
1. ✅ Your quiz will load questions from Supabase
2. ✅ Scores will save to cloud
3. ✅ Works from any computer with internet
4. ✅ All 4 players can connect from different WiFi networks

---

## 📝 **Quick Reference:**

### **Connection String Format:**
```
jdbc:postgresql://[HOST]:[PORT]/postgres
```

### **Common Ports:**
- Supabase Pooler: `6543` (recommended)
- Direct Connection: `5432`

### **Test Connection:**
```bash
# In IntelliJ terminal
mvn test-compile exec:java -Dexec.mainClass="com.example.retroquiz.Backend.CloudDatabaseConnection"
```

---

## 🐛 **Troubleshooting:**

### **Error: "Driver not found"**
→ Maven didn't download PostgreSQL driver
→ Solution: Right-click `pom.xml` → Maven → Reload Project

### **Error: "Connection refused"**
→ DB_URL is wrong
→ Solution: Double-check connection string from Supabase

### **Error: "Authentication failed"**
→ DB_PASSWORD is wrong
→ Solution: Check password in Supabase Settings

### **Error: "Unknown host"**
→ Internet connection issue
→ Solution: Check your internet connection

---

**Ready? Update `CloudDatabaseConnection.java` with your Supabase details and test it! 🚀**
