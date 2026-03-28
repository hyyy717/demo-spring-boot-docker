package com.example.LuuThanhHuy_2280601164.controller;

import com.example.LuuThanhHuy_2280601164.entity.Category;
import com.example.LuuThanhHuy_2280601164.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    // 1. HIỂN THỊ DANH SÁCH THỂ LOẠI
    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "admin/categories";
    }

    // 2. THÊM MỚI THỂ LOẠI (Hỗ trợ Real-time đồng bộ)
    @PostMapping("/add")
    public String addCategory(@RequestParam String name) {
        if (name != null && !name.trim().isEmpty()) {
            // Kiểm tra trùng tên trước khi lưu để tránh lỗi SQL
            if (categoryRepository.findByName(name.trim()) == null) {
                Category cat = new Category();
                cat.setName(name.trim());
                categoryRepository.save(cat);
            }
        }
        // Redirect về trang quản lý, giao diện script ở home.html sẽ tự nhận diện thay đổi qua hash
        return "redirect:/admin/categories";
    }

    // 3. CẬP NHẬT THỂ LOẠI (Sửa tên)
    @PostMapping("/edit")
    public String editCategory(@RequestParam Long id, @RequestParam String name) {
        Category existingCat = categoryRepository.findById(id).orElse(null);
        if (existingCat != null && name != null && !name.trim().isEmpty()) {
            existingCat.setName(name.trim());
            categoryRepository.save(existingCat);
        }
        return "redirect:/admin/categories";
    }

    // 4. XÓA THỂ LOẠI
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            // Có thể thêm báo lỗi nếu thể loại đang có sách (ràng buộc FK)
            return "redirect:/admin/categories?error=conflict";
        }
        return "redirect:/admin/categories";
    }
}