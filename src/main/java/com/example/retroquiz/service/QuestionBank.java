package com.example.retroquiz.service;

import com.example.retroquiz.model.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Hardcoded question bank for offline/demo mode.
 * 
 * NO DATABASE REQUIRED!
 * This allows the quiz to run on any computer without SQL Server setup.
 */
public class QuestionBank {

        /**
         * Get a list of hardcoded questions for demo/offline mode.
         * 
         * @param count Number of questions to return
         * @return List of questions (shuffled)
         */
        public static List<Question> getHardcodedQuestions(int count) {
                List<Question> allQuestions = new ArrayList<>();

                // Add 15 sample questions
                allQuestions.add(new Question(1,
                                "What year was the first computer mouse invented?",
                                "1964", "1970", "1980", "1990",
                                'A', "Technology", "EASY"));

                allQuestions.add(new Question(2,
                                "Which programming language was created by James Gosling?",
                                "Python", "Java", "C++", "Ruby",
                                'B', "Programming", "MEDIUM"));

                allQuestions.add(new Question(3,
                                "What does CPU stand for?",
                                "Central Processing Unit", "Computer Personal Unit",
                                "Central Program Utility", "Computer Processing Utility",
                                'A', "Hardware", "EASY"));

                allQuestions.add(new Question(4,
                                "In what year was Microsoft founded?",
                                "1975", "1980", "1985", "1990",
                                'A', "History", "MEDIUM"));

                allQuestions.add(new Question(5,
                                "What is the maximum value of a byte?",
                                "128", "255", "256", "512",
                                'B', "Computer Science", "MEDIUM"));

                allQuestions.add(new Question(6,
                                "Which company developed the first graphical web browser?",
                                "Microsoft", "Netscape", "Apple", "IBM",
                                'B', "Internet", "MEDIUM"));

                allQuestions.add(new Question(7,
                                "What does RAM stand for?",
                                "Random Access Memory", "Read Access Memory",
                                "Rapid Access Memory", "Real Access Memory",
                                'A', "Hardware", "EASY"));

                allQuestions.add(new Question(8,
                                "Who is known as the father of computers?",
                                "Alan Turing", "Charles Babbage", "Bill Gates", "Steve Jobs",
                                'B', "History", "MEDIUM"));

                allQuestions.add(new Question(9,
                                "What is the binary equivalent of decimal 10?",
                                "1010", "1100", "1001", "1111",
                                'A', "Computer Science", "HARD"));

                allQuestions.add(new Question(10,
                                "Which protocol is used for secure web browsing?",
                                "HTTP", "FTP", "HTTPS", "SMTP",
                                'C', "Networking", "MEDIUM"));

                allQuestions.add(new Question(11,
                                "What does HTML stand for?",
                                "Hyper Text Markup Language", "High Tech Modern Language",
                                "Home Tool Markup Language", "Hyperlinks and Text Markup Language",
                                'A', "Web Development", "EASY"));

                allQuestions.add(new Question(12,
                                "In which year was the World Wide Web invented?",
                                "1989", "1991", "1993", "1995",
                                'A', "Internet", "MEDIUM"));

                allQuestions.add(new Question(13,
                                "What is the name of the first electronic computer?",
                                "ENIAC", "UNIVAC", "EDVAC", "MARK I",
                                'A', "History", "HARD"));

                allQuestions.add(new Question(14,
                                "Which company created the Java programming language?",
                                "Microsoft", "Sun Microsystems", "Oracle", "IBM",
                                'B', "Programming", "MEDIUM"));

                allQuestions.add(new Question(15,
                                "What does SQL stand for?",
                                "Structured Query Language", "Simple Query Language",
                                "Standard Query Language", "System Query Language",
                                'A', "Database", "EASY"));

                // Shuffle the questions
                Collections.shuffle(allQuestions);

                // Return requested number of questions
                return allQuestions.subList(0, Math.min(count, allQuestions.size()));
        }

        /**
         * Get all hardcoded questions.
         * 
         * @return List of all questions
         */
        public static List<Question> getAllQuestions() {
                return getHardcodedQuestions(15);
        }
}
