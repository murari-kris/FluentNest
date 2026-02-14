package com.ecom.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.model.User;
import com.ecom.repository.UserRepository;

@RestController
@RequestMapping("/api")
public class AcademyApiController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/leaderboard")
    public List<User> getLeaderboard() {
        return userRepository.findTop10ByOrderByXpDesc();
    }

    @PostMapping("/user/update-xp")
    public ResponseEntity<?> updateXP(@RequestBody Map<String, Integer> data, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).body("Not Logged In");
        
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        
        int pointsToAdd = data.getOrDefault("xpValue", 0);
        user.setXp((user.getXp() != null ? user.getXp() : 0) + pointsToAdd);
        userRepository.save(user);
        
        return ResponseEntity.ok(Map.of("currentXp", user.getXp()));
    }
}