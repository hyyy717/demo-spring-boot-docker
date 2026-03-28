package com.example.LuuThanhHuy_2280601164.repository;

import com.example.LuuThanhHuy_2280601164.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name); // Hàm tự tạo để tìm Role theo tên
}