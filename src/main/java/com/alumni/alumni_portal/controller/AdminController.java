package com.alumni.alumni_portal.controller;

import com.alumni.alumni_portal.model.PrivateMessage;
import com.alumni.alumni_portal.model.User;
import com.alumni.alumni_portal.repository.PrivateMessageRepository;
import com.alumni.alumni_portal.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PrivateMessageRepository privateMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
        
        User user = (User) session.getAttribute("loggedUser");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        // Get all private messages for admin view
        List<PrivateMessage> allMessages = privateMessageRepository.findAllByOrderBySentDateDesc();
        
        // Get statistics
        long totalMessages = allMessages.size();
        long unreadMessages = allMessages.stream().filter(m -> !m.getIsRead()).count();
        long totalUsers = userRepository.count();
        
        model.addAttribute("messages", allMessages);
        model.addAttribute("totalMessages", totalMessages);
        model.addAttribute("unreadMessages", unreadMessages);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("adminUser", user);
        
        return "admin-dashboard";
    }

    @GetMapping("/messages")
    public String adminMessages(Model model, HttpSession session) {
        
        User user = (User) session.getAttribute("loggedUser");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        List<PrivateMessage> allMessages = privateMessageRepository.findAllByOrderBySentDateDesc();
        model.addAttribute("messages", allMessages);
        model.addAttribute("adminUser", user);
        
        return "admin-messages";
    }

    @PostMapping("/messages/mark-read/{messageId}")
    public String adminMarkAsRead(@PathVariable Long messageId, HttpSession session) {
        
        User user = (User) session.getAttribute("loggedUser");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        PrivateMessage message = privateMessageRepository.findById(messageId).orElse(null);
        if (message != null) {
            message.setIsRead(true);
            privateMessageRepository.save(message);
        }
        
        return "redirect:/admin/messages";
    }
}
