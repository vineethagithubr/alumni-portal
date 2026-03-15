package com.alumni.alumni_portal.controller;

import com.alumni.alumni_portal.model.*;
import com.alumni.alumni_portal.repository.*;
import com.alumni.alumni_portal.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    PrivateMessageRepository privateMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private JobRepository jobRepository;

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        return userService.isAdmin(user);
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("loggedUser");
        
        // Get all private messages for admin view
        List<PrivateMessage> allMessages = privateMessageRepository.findAllByOrderBySentDateDesc();
        
        // Get all public messages for admin view
        List<Message> allPublicMessages = messageRepository.findAllByOrderByIdDesc();
        
        // Get all events and jobs for admin view
        List<Event> allEvents = eventRepository.findAllByOrderByIdDesc();
        List<Job> allJobs = jobRepository.findAllByOrderByIdDesc();
        
        // Get statistics
        long totalMessages = allMessages.size();
        long unreadMessages = allMessages.stream().filter(m -> !m.getIsRead()).count();
        long totalUsers = userRepository.count();
        long totalEvents = eventRepository.count();
        long totalJobs = jobRepository.count();
        long totalPublicMessages = messageRepository.count();
        
        model.addAttribute("messages", allMessages);
        model.addAttribute("publicMessages", allPublicMessages);
        model.addAttribute("events", allEvents);
        model.addAttribute("jobs", allJobs);
        model.addAttribute("totalMessages", totalMessages);
        model.addAttribute("unreadMessages", unreadMessages);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("totalJobs", totalJobs);
        model.addAttribute("totalPublicMessages", totalPublicMessages);
        model.addAttribute("adminUser", user);
        
        return "admin-dashboard";
    }

    @GetMapping("/messages")
    public String adminMessages(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("loggedUser");
        List<PrivateMessage> allMessages = privateMessageRepository.findAllByOrderBySentDateDesc();
        model.addAttribute("messages", allMessages);
        model.addAttribute("adminUser", user);
        
        return "admin-messages";
    }

    @GetMapping("/users")
    public String adminUsers(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("loggedUser");
        List<User> allUsers = userService.getAllUsers();
        model.addAttribute("users", allUsers);
        model.addAttribute("adminUser", user);
        
        return "admin-users";
    }

    @GetMapping("/events")
    public String adminEvents(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("loggedUser");
        List<Event> allEvents = eventRepository.findAllByOrderByIdDesc();
        model.addAttribute("events", allEvents);
        model.addAttribute("adminUser", user);
        
        return "admin-events";
    }

    @GetMapping("/jobs")
    public String adminJobs(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("loggedUser");
        List<Job> allJobs = jobRepository.findAllByOrderByIdDesc();
        model.addAttribute("jobs", allJobs);
        model.addAttribute("adminUser", user);
        
        return "admin-jobs";
    }

    @GetMapping("/public-messages")
    public String adminPublicMessages(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("loggedUser");
        List<Message> allMessages = messageRepository.findAllByOrderByIdDesc();
        model.addAttribute("messages", allMessages);
        model.addAttribute("adminUser", user);
        
        return "admin-public-messages";
    }

    // Delete operations
    @PostMapping("/public-messages/delete/{messageId}")
    public String deletePublicMessage(@PathVariable Long messageId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        messageRepository.deleteById(messageId);
        redirectAttributes.addFlashAttribute("success", "Public message deleted successfully.");
        return "redirect:/admin/public-messages";
    }

    @PostMapping("/events/delete/{eventId}")
    public String deleteEvent(@PathVariable Long eventId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        eventRepository.deleteById(eventId);
        redirectAttributes.addFlashAttribute("success", "Event deleted successfully.");
        return "redirect:/admin/events";
    }

    @PostMapping("/jobs/delete/{jobId}")
    public String deleteJob(@PathVariable Long jobId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        jobRepository.deleteById(jobId);
        redirectAttributes.addFlashAttribute("success", "Job deleted successfully.");
        return "redirect:/admin/jobs";
    }

    @PostMapping("/users/delete/{userId}")
    public String deleteUser(@PathVariable Long userId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        User currentUser = (User) session.getAttribute("loggedUser");
        if (currentUser.getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete your own account.");
            return "redirect:/admin/users";
        }

        userService.deleteUser(userId);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully.");
        return "redirect:/admin/users";
    }
    
    @GetMapping("/users/add")
    public String showAddUserForm(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("user", new User());
        return "admin-add-user";
    }
    
    @PostMapping("/users/add")
    public String addUser(@RequestParam String email, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/dashboard";
        }
        
        // Check if email already exists
        List<User> existingUsers = userRepository.findByEmail(email);
        if (!existingUsers.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User with this email already exists.");
            return "redirect:/admin/users/add";
        }
        
        // Create new user with default password
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword("user@123"); // Default password
        newUser.setName("New User"); // Default name, can be updated later
        newUser.setBranch("TBD"); // Default branch
        newUser.setPassoutYear(2024); // Default year
        newUser.setRole(User.Role.USER); // Regular user role
        
        userService.registerUser(newUser);
        
        redirectAttributes.addFlashAttribute("success", "User added successfully with email: " + email + " and default password: user@123");
        return "redirect:/dashboard";
    }

    @PostMapping("/users/role/{userId}")
    public String updateUserRole(@PathVariable Long userId, @RequestParam User.Role role, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        User currentUser = (User) session.getAttribute("loggedUser");
        if (currentUser.getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("error", "Cannot change your own role.");
            return "redirect:/admin/users";
        }

        userService.updateUserRole(userId, role);
        redirectAttributes.addFlashAttribute("success", "User role updated successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/messages/mark-read/{messageId}")
    public String adminMarkAsRead(@PathVariable Long messageId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        PrivateMessage message = privateMessageRepository.findById(messageId).orElse(null);
        if (message != null) {
            message.setIsRead(true);
            privateMessageRepository.save(message);
        }
        
        return "redirect:/admin/messages";
    }
}
