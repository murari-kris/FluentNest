package com.ecom.controller;

import com.ecom.model.User;
import com.ecom.model.UserProfile;
import com.ecom.repository.UserProfileRepository;
import com.ecom.service.UserService;
import com.ecom.util.ActiveUserStore;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    private final UserService userService;
    private final ActiveUserStore activeUserStore;
    private final UserProfileRepository profileRepository;

    public HomeController(UserService userService,
                          ActiveUserStore activeUserStore,
                          UserProfileRepository profileRepository) {
        this.userService = userService;
        this.activeUserStore = activeUserStore;
        this.profileRepository = profileRepository;
    }

    @GetMapping({"/home", "/"})
    public String homePage(Model model, HttpSession session) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // 🔒 Safety check
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getName())) {

            return "redirect:/login";
        }

        String username = authentication.getName();

        // ✅ Store username in session
        session.setAttribute("username", username);

        // ✅ Add user to active users
        activeUserStore.addUser(username);

        // ✅ User MUST exist
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);

        // ✅ Fetch profile safely
        UserProfile profile =
                profileRepository.findByUser(user).orElse(null);

        String profileImage =
                (profile != null && profile.getProfileImage() != null)
                        ? profile.getProfileImage()
                        : "/images/default-avatar.png";

        model.addAttribute("profileImage", profileImage);

        // ✅ Active users list
        model.addAttribute("activeUsers", activeUserStore.getUsers());

        return "index"; // index.html
    }
}
