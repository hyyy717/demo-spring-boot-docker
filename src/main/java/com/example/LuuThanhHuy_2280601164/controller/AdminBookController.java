package com.example.LuuThanhHuy_2280601164.controller;

import com.example.LuuThanhHuy_2280601164.entity.Book;
import com.example.LuuThanhHuy_2280601164.repository.BookRepository;
import com.example.LuuThanhHuy_2280601164.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/admin")
public class AdminBookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public static String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/images/";

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/add-book";
    }

    @PostMapping("/add")
    public String saveBook(@ModelAttribute Book book, @RequestParam("file") MultipartFile file, RedirectAttributes redirect) {
        saveImage(book, file);
        if (book.getQuantity() < 0) {
            book.setQuantity(0);
        }
        bookRepository.save(book);
        redirect.addFlashAttribute("successMsg", "Đã thêm sách mới thành công!");
        return "redirect:/home";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id).orElse(null);
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/edit-book";
    }

    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book book, @RequestParam("file") MultipartFile file, RedirectAttributes redirect) {
        Book existingBook = bookRepository.findById(id).orElse(null);
        if (existingBook != null) {
            existingBook.setTitle(book.getTitle());
            existingBook.setAuthor(book.getAuthor());
            existingBook.setPrice(book.getPrice());
            existingBook.setCategory(book.getCategory());
            existingBook.setDescription(book.getDescription());
            existingBook.setQuantity(book.getQuantity() >= 0 ? book.getQuantity() : 0);

            if (!file.isEmpty()) {
                saveImage(existingBook, file);
            }
            bookRepository.save(existingBook);
            redirect.addFlashAttribute("successMsg", "Đã cập nhật thông tin sách thành công!");
        }
        return "redirect:/home";
    }

    // ĐÃ FIX: Bắt lỗi xóa sách khi sách nằm trong hóa đơn
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            bookRepository.deleteById(id);
            redirect.addFlashAttribute("successMsg", "Đã xóa sách khỏi hệ thống!");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMsg", "Không thể xóa! Sách này đã nằm trong lịch sử mua hàng của khách. Vui lòng cập nhật số lượng về 0 thay vì xóa.");
        }
        return "redirect:/home";
    }

    private void saveImage(Book book, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                File dir = new File(UPLOAD_DIR);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(filePath, file.getBytes());
                book.setImageName(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}