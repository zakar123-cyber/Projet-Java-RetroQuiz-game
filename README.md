# RetroQuiz - JavaFX Quiz Application

RetroQuiz is an interactive and engaging quiz application built with Java and JavaFX. It allows users to participate in quizzes, manage questions, and compete on leaderboards. The application supports both administrative tasks (like creating quizzes and managing users) and player activities (joining lobbies, playing quizzes).

## 🚀 Features

### Core Gameplay
- **Interactive Quizzes**: Real-time quiz taking with multiple choice questions.
- **Lobby System**: Players can join lobbies (`MainLobby`, `QuizLobby`) before starting a game.
- **Guest Access**: Join quickly as a guest without a full account.
- **Results & Leaderboards**: View your score and compare it with others on the global leaderboard.

### Administration
- **Admin Dashboard**: Central hub for administrative tasks.
- **Quiz Creation**: Tools to create and configure new quizzes.
- **Question Management**: Add, edit, and delete questions from the database.
- **User Management**: Manage registered users.

## 🛠️ Technology Stack

- **Language**: Java 17+
- **Framework**: JavaFX (UI)
- **Build Tool**: Maven
- **Database**: 
  - SQL Server (MSSQL)
  - PostgreSQL (Supabase support)
- **Testing**: JUnit 5

## 📁 Project Structure

```
src/main/java/com/example/retroquiz
├── controller      # JavaFX Controllers (UI Logic)
├── dao             # Data Access Objects
├── model           # Domain Models (User, Question, etc.)
├── repository      # Database Repositories
├── service         # Business Logic Services
├── util            # Utility Classes
└── HelloApplication.java  # Main Entry Point
```

## ⚙️ Getting Started

### Prerequisites
- [Java JDK 17](https://www.oracle.com/java/technologies/downloads/) or higher.
- [Maven](https://maven.apache.org/) installed.
- SQL Server or PostgreSQL database setup.

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/zakar123-cyber/Projet-Java-RetroQuiz-game.git
   cd Projet-Java-RetroQuiz-game
   ```

2. **Configure Database**
   - Ensure your database is running.
   - Update connection strings in your configuration/DAO classes if necessary.

3. **Build the Project**
   ```bash
   mvn clean install
   ```

### Running the Application

You can run the application using Maven:

```bash
mvn javafx:run
```

## 📸 Usage

- **Start the App**: Launch the application to see the "RetroQuiz Menu".
- **Login/Register**: Sign in as a Host or User, or continue as a Guest.
- **Play**: Join a quiz lobby and test your knowledge!
- **Admin**: Log in with admin credentials to access the Dashboard to manage content.

## 🤝 Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
