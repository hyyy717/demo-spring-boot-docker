package com.example.LuuThanhHuy_2280601164.service;

import com.example.LuuThanhHuy_2280601164.entity.Book;
import com.example.LuuThanhHuy_2280601164.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    // Đường dẫn lưu ảnh trong dự án
    private final String UPLOAD_DIR = "src/main/resources/static/images/";

    public List<Book> getAllBooks() { return bookRepository.findAll(); }

    public void saveBook(Book book, MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            // Tạo tên file duy nhất để không bị trùng (ví dụ: a1b2-tenanh.jpg)
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);

            // Tạo thư mục nếu chưa có
            Files.createDirectories(path.getParent());
            // Lưu file vào thư mục static/images
            Files.write(path, file.getBytes());

            book.setImageName(fileName);
        }
        bookRepository.save(book);
    }

    public void deleteBook(Long id) { bookRepository.deleteById(id); }

    public Book getBookById(Long id) { return bookRepository.findById(id).orElse(null); }
}