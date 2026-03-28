package com.example.LuuThanhHuy_2280601164.controller;

import com.example.LuuThanhHuy_2280601164.entity.Role;
import com.example.LuuThanhHuy_2280601164.entity.User;
import com.example.LuuThanhHuy_2280601164.repository.RoleRepository;
import com.example.LuuThanhHuy_2280601164.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName("ROLE_USER");
        user.setRoles(List.of(userRole));
        userRepository.save(user);
        return "redirect:/login?success";
    }

    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser, Principal principal) {
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username);

        if (currentUser != null) {
            currentUser.setFullName(updatedUser.getFullName());
            currentUser.setPhone(updatedUser.getPhone());
            currentUser.setDateOfBirth(updatedUser.getDateOfBirth());
            currentUser.setGender(updatedUser.getGender());
            currentUser.setAddress(updatedUser.getAddress());
            userRepository.save(currentUser);
        }
        return "redirect:/profile?success";
    }

    // --- API KIỂM TRA TRẠNG THÁI KHÓA REAL-TIME ---
    @GetMapping("/api/check-status")
    @ResponseBody
    public String checkStatus(Principal principal) {
        if (principal == null) return "logged_out";
        User user = userRepository.findByUsername(principal.getName());
        if (user != null && !user.isActive()) {
            return "locked";
        }
        return "active";
    }
}