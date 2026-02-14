package com.ecom.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/practice")
public class PracticeController {

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startPractice() {

        Map<String, String> response = new HashMap<>();
        response.put("status", "PAIRED");
        response.put("roomId", "room123");

        return ResponseEntity.ok(response);
    }
}
