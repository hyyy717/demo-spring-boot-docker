package com.example.LuuThanhHuy_2280601164.repository;

import com.example.LuuThanhHuy_2280601164.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Spring Boot sẽ tự động tạo các hàm save(), delete(), findAll() cho bạn
}