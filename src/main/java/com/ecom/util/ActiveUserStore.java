package com.ecom.util;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.Set;

@Component
public class ActiveUserStore {

    // ✅ Using ConcurrentHashMap.newKeySet() makes the Set thread-safe
    private final Set<String> users = ConcurrentHashMap.newKeySet();

    public Set<String> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    public void addUser(String username) {
        if (username != null) {
            users.add(username);
        }
    }

    public void removeUser(String username) {
        if (username != null) {
            users.remove(username);
        }
    }
}