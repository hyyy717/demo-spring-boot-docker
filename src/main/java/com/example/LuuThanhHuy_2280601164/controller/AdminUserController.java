package com.example.LuuThanhHuy_2280601164.controller;

import com.example.LuuThanhHuy_2280601164.entity.User;
import com.example.LuuThanhHuy_2280601164.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRegistry sessionRegistry; // Kéo bộ theo dõi phiên vào

    @GetMapping
    public String listUsers(Model model) {
        List<User> regularUsers = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .noneMatch(role -> role.getName().equals("ROLE_ADMIN")))
                .collect(Collectors.toList());

        model.addAttribute("users", regularUsers);
        return "admin/users";
    }

    // API Khóa / Mở Khóa tài khoản
    @PostMapping("/toggle-lock/{id}")
    public String toggleLock(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // Đảo ngược trạng thái (Đang true thành false, đang false thành true)
            user.setActive(!user.isActive());
            userRepository.save(user);

            // NẾU BỊ KHÓA -> TÌM VÀ ĐÁ NGƯỜI DÙNG ĐÓ RA KHỎI HỆ THỐNG
            if (!user.isActive()) {
                for (Object principal : sessionRegistry.getAllPrincipals()) {
                    if (principal instanceof org.springframework.security.core.userdetails.User) {
                        org.springframework.security.core.userdetails.User loggedInUser = (org.springframework.security.core.userdetails.User) principal;
                        if (loggedInUser.getUsername().equals(user.getUsername())) {
                            // Hủy toàn bộ phiên hoạt động của người này
                            for (SessionInformation sessionInfo : sessionRegistry.getAllSessions(principal, false)) {
                                sessionInfo.expireNow();
                            }
                        }
                    }
                }
            }
        }
        return "redirect:/admin/users";
    }
}