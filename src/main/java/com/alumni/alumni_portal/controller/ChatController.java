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

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private PrivateMessageRepository privateMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/chat/{userId}")
    public String openChat(@PathVariable Long userId, Model model, HttpSession session) {
        
        User currentUser = (User) session.getAttribute("loggedUser");
        
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        User chatUser = userRepository.findById(userId).orElse(null);
        if (chatUser == null) {
            return "redirect:/directory";
        }
        
        // Get conversation between current user and chat user
        List<PrivateMessage> messages = privateMessageRepository.findConversationBetweenUsers(currentUser, chatUser);
        
        // Mark unread messages as read
        messages.stream()
            .filter(m -> m.getReceiver().getId().equals(currentUser.getId()) && !m.getIsRead())
            .forEach(m -> {
                m.setIsRead(true);
                privateMessageRepository.save(m);
            });
        
        model.addAttribute("chatUser", chatUser);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("chatMessages", messages);
        
        // Get unread count for badge
        long unreadCount = privateMessageRepository.countUnreadMessages(currentUser);
        model.addAttribute("unreadCount", unreadCount);
        
        return "chat";
    }

    @PostMapping("/messages/send")
    public String sendMessage(@RequestParam Long receiverId,
                              @RequestParam String content,
                              HttpSession session) {
        
        User sender = (User) session.getAttribute("loggedUser");
        
        if (sender == null) {
            return "redirect:/login";
        }
        
        User receiver = userRepository.findById(receiverId).orElse(null);
        if (receiver == null) {
            return "redirect:/directory";
        }
        
        PrivateMessage message = new PrivateMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setSentDate(LocalDateTime.now());
        message.setIsRead(false);
        
        privateMessageRepository.save(message);
        
        return "redirect:/chat/" + receiverId;
    }

    @GetMapping("/chat/unread-count")
    @ResponseBody
    public long getUnreadCount(HttpSession session) {
        
        User currentUser = (User) session.getAttribute("loggedUser");
        
        if (currentUser == null) {
            return 0;
        }
        
        return privateMessageRepository.countUnreadMessages(currentUser);
    }
}
