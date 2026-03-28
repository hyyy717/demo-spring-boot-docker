package com.example.LuuThanhHuy_2280601164.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cart_items")
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Của người dùng nào

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book; // Sản phẩm nào

    private int quantity; // Số lượng bao nhiêu
}