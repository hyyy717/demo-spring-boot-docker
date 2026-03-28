package com.example.LuuThanhHuy_2280601164.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "books")
@Data // Tự động tạo Getter/Setter nhờ thư viện Lombok
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private Double price;
    private String description;
    // Thêm dòng này vào dưới các thuộc tính (dưới price, description...)
    @Column(columnDefinition = "integer default 0")
    private int quantity;

    // Thêm Getter và Setter
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    private String imageName;

    // Trường mới: Phân loại sách (Category)
    private String category;
}