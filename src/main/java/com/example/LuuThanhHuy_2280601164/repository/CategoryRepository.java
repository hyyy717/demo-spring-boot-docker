package com.example.LuuThanhHuy_2280601164.repository;

import com.example.LuuThanhHuy_2280601164.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Thêm dòng này để Spring Boot tự động tạo lệnh tìm kiếm Thể loại theo Tên
    Category findByName(String name);

}