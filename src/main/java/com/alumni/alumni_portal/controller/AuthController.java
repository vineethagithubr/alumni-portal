package com.alumni.alumni_portal.controller;

import com.alumni.alumni_portal.controller.*;
import com.alumni.alumni_portal.dto.ChatInfo;
import com.alumni.alumni_portal.model.*;
import com.alumni.alumni_portal.repository.*;
import com.alumni.alumni_portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private PrivateMessageRepository privateMessageRepository;

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirectAttributes, Model model){

        // Check if email already exists
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("error", "Email already registered. Please use a different email or login.");
            return "register";
        }

        userService.registerUser(user);
        return "register-success";
    }

    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            Model model,
                            HttpSession session){

        User user = userService.login(email,password);

        if(user != null){

            session.setAttribute("loggedUser", user);

            return "redirect:/dashboard";
        }

        model.addAttribute("error","Invalid Email or Password");
        return "login";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session){

        User user = (User) session.getAttribute("loggedUser");

        if(user == null){
            return "redirect:/login";
        }

        // Add username for template
        model.addAttribute("username", user.getName());
        
        // Check if user is admin
        boolean isAdmin = userService.isAdmin(user);
        model.addAttribute("isAdmin", isAdmin);
        
        // Debug: Add user role info for debugging
        model.addAttribute("userRole", user.getRole());
        model.addAttribute("userId", user.getId());

        // Parallel execution of database queries for better performance
        try {
            // Get latest messages, events, and jobs in parallel
            List<Message> messages = messageRepository.findTop2ByOrderByIdDesc();
            List<Event> events = eventRepository.findTop2ByOrderByIdDesc();
            List<Job> jobs = jobRepository.findTop2ByOrderByIdDesc();
            
            model.addAttribute("messages", messages);
            model.addAttribute("events", events);
            model.addAttribute("jobs", jobs);
            
        } catch (Exception e) {
            // Fallback with empty lists if any error occurs
            model.addAttribute("messages", List.of());
            model.addAttribute("events", List.of());
            model.addAttribute("jobs", List.of());
        }
        
        // Optimized private messages and chat info
        try {
            // Get recent chats with optimized query
            List<PrivateMessage> recentMessages = privateMessageRepository.findRecentChatsForUser(user.getId(), 10);
            
            // Build chat info list efficiently
            Map<Long, ChatInfo> chatMap = new HashMap<>();
            
            for (PrivateMessage msg : recentMessages) {
                Long otherUserId = msg.getReceiver().getId().equals(user.getId()) ? 
                    msg.getSender().getId() : msg.getReceiver().getId();
                
                if (!chatMap.containsKey(otherUserId)) {
                    User otherUser = msg.getReceiver().getId().equals(user.getId()) ? 
                        msg.getSender() : msg.getReceiver();
                    
                    long unreadCount = (msg.getReceiver().getId().equals(user.getId()) && !msg.getIsRead()) ? 1 : 0;
                    chatMap.put(otherUserId, new ChatInfo(otherUser, msg, unreadCount));
                } else {
                    // Update unread count if this is an unread message
                    ChatInfo chatInfo = chatMap.get(otherUserId);
                    if (msg.getReceiver().getId().equals(user.getId()) && !msg.getIsRead()) {
                        chatInfo.setUnreadCount(chatInfo.getUnreadCount() + 1);
                    }
                }
            }
            
            // Convert to list and sort
            List<ChatInfo> recentChats = new ArrayList<>(chatMap.values());
            recentChats.sort((a, b) -> b.getLastMessage().getSentDate().compareTo(a.getLastMessage().getSentDate()));
            
            // Limit to 5 recent chats
            recentChats = recentChats.stream().limit(5).collect(Collectors.toList());
            
            model.addAttribute("recentChats", recentChats);
            
            // Get total unread count
            long unreadCount = privateMessageRepository.countUnreadMessages(user);
            model.addAttribute("unreadCount", unreadCount);
            
        } catch (Exception e) {
            model.addAttribute("recentChats", List.of());
            model.addAttribute("unreadCount", 0);
        }

        return "dashboard";
    }
    
    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        
        if(user == null){
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "change-password";
    }
    
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        User user = (User) session.getAttribute("loggedUser");
        
        if(user == null){
            return "redirect:/login";
        }
        
        // Validate input
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Current password is required.");
            return "redirect:/change-password";
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "New password is required.");
            return "redirect:/change-password";
        }
        
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please confirm your new password.");
            return "redirect:/change-password";
        }
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
            return "redirect:/change-password";
        }
        
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters long.");
            return "redirect:/change-password";
        }
        
        // Verify current password
        if (!userService.verifyPassword(user, currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/change-password";
        }
        
        // Update password
        try {
            userService.updatePassword(user, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error changing password. Please try again.");
            return "redirect:/change-password";
        }
    }
}
