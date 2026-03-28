package com.example.LuuThanhHuy_2280601164.controller;

import com.example.LuuThanhHuy_2280601164.repository.CategoryRepository;
import com.example.LuuThanhHuy_2280601164.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StoreController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryRepository categoryRepository;

    // Đường dẫn chung cho cả User và Admin
    @GetMapping({"/", "/home"})
    public String storefront(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("categories", categoryRepository.findAll()); // Kéo danh sách thể loại từ DB
        return "home"; // Gọi file home.html
    }
}