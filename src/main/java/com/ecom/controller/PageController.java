package com.ecom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/main")
    public String mainPage() {
        return "main"; // corresponds to templates/main.html (if using Thymeleaf)
    }

    @GetMapping("/lesson")
    public String lessonPage() {
        return "lesson"; // corresponds to templates/lesson.html
    }

    @GetMapping("/quiz")
    public String quizPage() {
        return "quiz"; // corresponds to templates/quiz.html
    }
    
    @GetMapping("/vocabulary")
    public String vocabularyPage() {
        return "vocabulary"; // corresponds to templates/quiz.html
    }
    
   
    
    @GetMapping("interview")
    public String interviewPage() {
        return "interview"; // corresponds to templates/quiz.html
    }
    
    @GetMapping("conversation")
    public String conversationPage() {
        return "conversation"; // corresponds to templates/quiz.html
    }
    
    @GetMapping("examples")
    public String examplesPage() {
        return "examples"; // corresponds to templates/quiz.html
    }
    
    @GetMapping("challenge")
    public String challengePage() {
        return "challenge"; // corresponds to templates/quiz.html
    }
    
    @GetMapping("leaderboard")
    public String leaderboardPage() {
        return "leaderboard"; // corresponds to templates/quiz.html
    }
    
    @GetMapping("start")
    public String startlessionPage() {
        return "start"; // corresponds to templates/quiz.html
    }
    
    @GetMapping("pronunciation")
    public String pronunciationPage() {
        return "pronunciation"; // corresponds to templates/quiz.html
    }
    
    @GetMapping("words")
    public String wordsPage() {
        return "words"; // corresponds to templates/quiz.html
    }
    
    
    @GetMapping("listen")
    public String listenPage() {
        return "listen"; // corresponds to templates/quiz.html
    }
    
    
    @GetMapping("/audio")
    public String audioForm() {
        return "Audio";   // Audio.html
    }
    
    
   
    
    
}
