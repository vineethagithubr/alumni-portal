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
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
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
    public String registerUser(@ModelAttribute User user){

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

        // Get latest 2 messages, events, and jobs with fallback
        try {
            List<Message> messages = messageRepository.findTop2ByOrderByPublishedDateDesc();
            System.out.println("Found " + messages.size() + " messages");
            model.addAttribute("messages", messages);
        } catch (Exception e) {
            System.out.println("Error with published date, using fallback: " + e.getMessage());
            model.addAttribute("messages", messageRepository.findTop2ByOrderByIdDesc());
        }
        
        // Get private messages for the logged-in user
        try {
            List<PrivateMessage> privateMessages = privateMessageRepository.findByReceiverOrderBySentDateDesc(user);
            System.out.println("Found " + privateMessages.size() + " private messages");
            model.addAttribute("privateMessages", privateMessages);
            
            // Get unread count for chat badge
            long unreadCount = privateMessageRepository.countUnreadMessages(user);
            model.addAttribute("unreadCount", unreadCount);
            
            // Prepare recent chats list
            List<ChatInfo> recentChats = new ArrayList<>();
            
            // Get all messages where user is either sender or receiver
            List<PrivateMessage> allUserMessages = new ArrayList<>();
            allUserMessages.addAll(privateMessageRepository.findByReceiverOrderBySentDateDesc(user));
            allUserMessages.addAll(privateMessageRepository.findBySenderOrderBySentDateDesc(user));
            
            // Group by other user and get latest message
            allUserMessages.stream()
                .collect(Collectors.groupingBy(msg -> 
                    msg.getReceiver().getId().equals(user.getId()) ? msg.getSender() : msg.getReceiver()))
                .forEach((otherUser, messages) -> {
                    PrivateMessage latestMessage = messages.get(0); // Already ordered by date desc
                    long unreadFromUser = messages.stream()
                        .filter(m -> m.getReceiver().getId().equals(user.getId()) && !m.getIsRead())
                        .count();
                    recentChats.add(new ChatInfo(otherUser, latestMessage, unreadFromUser));
                });
            
            // Sort by latest message time, prioritizing received messages
            recentChats.sort((a, b) -> {
                int dateCompare = b.getLastMessage().getSentDate().compareTo(a.getLastMessage().getSentDate());
                if (dateCompare != 0) return dateCompare;
                
                // If same timestamp, prioritize received messages over sent messages
                boolean aIsReceived = a.getLastMessage().getReceiver().getId().equals(user.getId());
                boolean bIsReceived = b.getLastMessage().getReceiver().getId().equals(user.getId());
                
                if (aIsReceived && !bIsReceived) return -1;
                if (!aIsReceived && bIsReceived) return 1;
                return 0;
            });
            
            // Limit to 5 recent chats
            model.addAttribute("recentChats", recentChats.stream().limit(5).collect(Collectors.toList()));
            
        } catch (Exception e) {
            System.out.println("Error getting private messages: " + e.getMessage());
            model.addAttribute("privateMessages", List.of());
            model.addAttribute("unreadCount", 0);
            model.addAttribute("recentChats", List.of());
        }
        
        try {
            List<Event> events = eventRepository.findTop2ByOrderByPublishedDateDesc();
            System.out.println("Found " + events.size() + " events");
            model.addAttribute("events", events);
        } catch (Exception e) {
            System.out.println("Error with published date, using fallback: " + e.getMessage());
            model.addAttribute("events", eventRepository.findTop2ByOrderByIdDesc());
        }
        
        try {
            List<Job> jobs = jobRepository.findTop2ByOrderByPublishedDateDesc();
            System.out.println("Found " + jobs.size() + " jobs");
            model.addAttribute("jobs", jobs);
        } catch (Exception e) {
            System.out.println("Error with published date, using fallback: " + e.getMessage());
            model.addAttribute("jobs", jobRepository.findTop2ByOrderByIdDesc());
        }

        return "dashboard";
    }
}
