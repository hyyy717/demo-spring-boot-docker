package com.example.LuuThanhHuy_2280601164.repository;

import com.example.LuuThanhHuy_2280601164.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username); // Hàm tự tạo để tìm User đăng nhập
}