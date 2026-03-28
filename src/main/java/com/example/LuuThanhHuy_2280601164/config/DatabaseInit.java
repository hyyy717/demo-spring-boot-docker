package com.example.LuuThanhHuy_2280601164.config;

import com.example.LuuThanhHuy_2280601164.entity.Role;
import com.example.LuuThanhHuy_2280601164.entity.User;
import com.example.LuuThanhHuy_2280601164.repository.RoleRepository;
import com.example.LuuThanhHuy_2280601164.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseInit implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Tạo Role nếu chưa có
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }

        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }

        // 2. Tạo hoặc Mở khóa tài khoản Admin mặc định
        User admin = userRepository.findByUsername("admin");
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // Mã hóa mật khẩu
            admin.setEmail("admin@bookstore.com");
            admin.setFullName("Quản Trị Viên");
            admin.setActive(true); // Đảm bảo tài khoản được kích hoạt
            admin.setRoles(List.of(adminRole));
            userRepository.save(admin);
            System.out.println("Đã tạo tài khoản Admin thành công!");
        } else {
            // Cứu cánh: Nếu Admin cũ bị MySQL set isActive = false, thì bật lại thành true
            if (!admin.isActive()) {
                admin.setActive(true);
                userRepository.save(admin);
                System.out.println("Đã tự động mở khóa tài khoản Admin do lỗi Database cũ!");
            }
        }
    }
}