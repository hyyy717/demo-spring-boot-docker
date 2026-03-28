package com.example.LuuThanhHuy_2280601164.controller;

import com.example.LuuThanhHuy_2280601164.entity.*;
import com.example.LuuThanhHuy_2280601164.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CartAndOrderController {

    @Autowired private BookRepository bookRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;

    public static class CartItemDTO {
        private Book book;
        private int quantity;
        public CartItemDTO(Book book, int quantity) { this.book = book; this.quantity = quantity; }
        public Book getBook() { return book; }
        public void setBook(Book book) { this.book = book; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    // Hàm phụ trợ tính tổng số lượng sản phẩm
    private void updateCartTotal(HttpSession session, Map<Long, Integer> cart) {
        if (cart == null || cart.isEmpty()) {
            session.setAttribute("cartTotalQty", 0);
        } else {
            int total = cart.values().stream().mapToInt(Integer::intValue).sum();
            session.setAttribute("cartTotalQty", total);
        }
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long bookId, @RequestParam(defaultValue = "1") int quantity, HttpSession session, RedirectAttributes redirect) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book != null) {
            if (book.getQuantity() < quantity) {
                redirect.addFlashAttribute("errorMsg", "Không đủ số lượng! Trong kho chỉ còn " + book.getQuantity() + " quyển.");
                return "redirect:/home";
            }
            book.setQuantity(book.getQuantity() - quantity);
            bookRepository.save(book);

            Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
            if (cart == null) cart = new HashMap<>();
            cart.put(bookId, cart.getOrDefault(bookId, 0) + quantity);

            session.setAttribute("cart", cart);
            updateCartTotal(session, cart); // Cập nhật tổng

            redirect.addFlashAttribute("successMsg", "Đã thêm " + quantity + " quyển '" + book.getTitle() + "' vào giỏ hàng!");
        }
        return "redirect:/home";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long id, @RequestParam String action, HttpSession session, RedirectAttributes redirect) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null && cart.containsKey(id)) {
            Book book = bookRepository.findById(id).orElse(null);
            if (book != null) {
                int currentCartQty = cart.get(id);
                if ("increase".equals(action)) {
                    if (book.getQuantity() > 0) {
                        book.setQuantity(book.getQuantity() - 1);
                        cart.put(id, currentCartQty + 1);
                        bookRepository.save(book);
                    } else {
                        redirect.addFlashAttribute("errorMsg", "Sách '" + book.getTitle() + "' đã hết sạch trong kho!");
                    }
                } else if ("decrease".equals(action)) {
                    if (currentCartQty > 1) {
                        book.setQuantity(book.getQuantity() + 1);
                        cart.put(id, currentCartQty - 1);
                        bookRepository.save(book);
                    } else {
                        book.setQuantity(book.getQuantity() + 1);
                        bookRepository.save(book);
                        cart.remove(id);
                    }
                }
                session.setAttribute("cart", cart);
                updateCartTotal(session, cart); // Cập nhật tổng
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeCart(@RequestParam Long id, HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null && cart.containsKey(id)) {
            int qtyToRestore = cart.get(id);
            cart.remove(id);

            Book book = bookRepository.findById(id).orElse(null);
            if (book != null) {
                book.setQuantity(book.getQuantity() + qtyToRestore);
                bookRepository.save(book);
            }
            session.setAttribute("cart", cart);
            updateCartTotal(session, cart); // Cập nhật tổng
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        List<CartItemDTO> cartItems = new ArrayList<>();
        double total = 0;

        if (cart != null) {
            for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
                Book book = bookRepository.findById(entry.getKey()).orElse(null);
                if (book != null) {
                    cartItems.add(new CartItemDTO(book, entry.getValue()));
                    total += book.getPrice() * entry.getValue();
                }
            }
        }
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", total);
        return "cart";
    }

    @PostMapping("/cart/checkout")
    @Transactional
    public String checkout(HttpSession session, Principal principal) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/cart";

        User user = userRepository.findByUsername(principal.getName());
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("Thanh toán thành công");

        double total = 0;
        List<OrderDetail> details = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Book book = bookRepository.findById(entry.getKey()).orElse(null);
            if (book == null) continue;
            int buyQty = entry.getValue();

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setBook(book);
            detail.setQuantity(buyQty);
            detail.setPrice(book.getPrice());
            details.add(detail);
            total += (book.getPrice() * buyQty);
        }

        order.setTotalAmount(total);
        order.setOrderDetails(details);
        orderRepository.save(order);

        session.removeAttribute("cart");
        session.setAttribute("cartTotalQty", 0); // Reset tổng

        return "redirect:/cart/bill/" + order.getId();
    }

    @GetMapping("/cart/bill/{orderId}")
    public String viewBill(@PathVariable Long orderId, Principal principal, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && order.getUser().getUsername().equals(principal.getName())) {
            model.addAttribute("order", order);
            return "bill";
        }
        return "redirect:/home";
    }

    @GetMapping("/history")
    public String viewHistory(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("orders", orderRepository.findByUserIdOrderByOrderDateDesc(user.getId()));
        return "history";
    }

    @GetMapping("/admin/orders")
    public String adminOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAllByOrderByOrderDateDesc());
        return "admin/orders";
    }
}