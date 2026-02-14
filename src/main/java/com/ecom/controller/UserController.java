package com.ecom.controller;

import com.ecom.model.User;
import com.ecom.service.UserService;
import com.ecom.util.ActiveUserStore;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private final UserService userService;
    private final ActiveUserStore activeUserStore;

    public UserController(UserService userService, ActiveUserStore activeUserStore) {
        this.userService = userService;
        this.activeUserStore = activeUserStore;
    }

    // Show all users
    @GetMapping("/users")
    public String getAllUsers(
            @RequestParam(value = "search", required = false) String search,
            Model model
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        // ✅ Fetch users (search if keyword provided)
        List<User> users;
        if (search != null && !search.isEmpty()) {
            users = userService.searchUsers(search);
        } else {
            users = userService.getAllUsers();
        }

        // Exclude admin & current user
        users = users.stream()
                .filter(u -> !u.getUsername().equalsIgnoreCase("admin"))
                .filter(u -> !u.getUsername().equalsIgnoreCase(currentUser))
                .collect(Collectors.toList());

        // Active users
        Set<String> activeUsers = activeUserStore.getUsers()
                .stream()
                .filter(u -> !u.equalsIgnoreCase(currentUser))
                .collect(Collectors.toSet());

        model.addAttribute("users", users);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("search", search);

        return "users"; // Thymeleaf template: users.html
    }
}
