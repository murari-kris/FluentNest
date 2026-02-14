package com.ecom.controller;

import com.ecom.model.User;
import com.ecom.model.UserProfile;
import com.ecom.repository.UserProfileRepository;
import com.ecom.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;

    public ProfileController(UserRepository userRepository,
                             UserProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    // ================= VIEW PROFILE =================
    @GetMapping
    public String profile(Model model, Principal principal) {

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = profileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUser(user);
                    return profileRepository.save(p);
                });

        model.addAttribute("profile", profile);
        return "profile";
    }

    // ================= EDIT PROFILE =================
    @GetMapping("/edit")
    public String editProfile(Model model, Principal principal) {

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        model.addAttribute("profile", profile);
        return "edit";
    }

    // ================= UPDATE PROFILE =================
    @PostMapping("/update")
    public String updateProfile(
            @ModelAttribute("profile") UserProfile formProfile,
            @RequestParam("imageFile") MultipartFile imageFile,
            Principal principal
    ) throws IOException {

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // -------- Update text fields --------
        profile.setName(formProfile.getName());
        profile.setNativeLanguage(formProfile.getNativeLanguage());
        profile.setEnglishLevel(formProfile.getEnglishLevel());
        profile.setPracticeLanguages(formProfile.getPracticeLanguages());
        profile.setProfession(formProfile.getProfession());
        profile.setFavoriteTopics(formProfile.getFavoriteTopics());
        profile.setGoals(formProfile.getGoals());
        profile.setAvailability(formProfile.getAvailability());

        // -------- Image upload (FIXED) --------
        if (imageFile != null && !imageFile.isEmpty()) {

            // Absolute + safe directory
            String uploadDir = System.getProperty("user.dir")
                    + File.separator + "uploads"
                    + File.separator + "profile";

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // 🔥 MUST
            }

            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            File destination = new File(dir, fileName);

            imageFile.transferTo(destination);

            // store only relative path / filename
            profile.setProfileImage("/uploads/profile/" + fileName);
        }

        profileRepository.save(profile);
        return "redirect:/profile";
    }
}
