package com.ecom.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Service
public class KeepAliveService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Runs every 12 minutes (720,000 ms) 
    // We use 12 mins to stay safely under Render's 15-minute sleep timer
    @Scheduled(fixedRate = 720000)
    public void keepAlive() {
        try {
            String url = "https://fluentnest.onrender.com/"; 
            
            // Adding a basic header so it looks like a legitimate request
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "FluentNest-KeepAlive-Bot");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            System.out.println("✅ Render Keep-Alive: Ping sent successfully at " + new java.util.Date());
        } catch (Exception e) {
            System.err.println("⚠️ Ping failed: " + e.getMessage());
        }
    }
}