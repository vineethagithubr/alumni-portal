package com.alumni.alumni_portal.controller;

import com.alumni.alumni_portal.dto.ChatInfo;
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
import java.util.ArrayList;
import java.util.stream.Collectors;

@Controller
public class PrivateMessageController {

    @Autowired
    private PrivateMessageRepository privateMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/messages/new/{receiverId}")
    public String newPrivateMessage(@PathVariable Long receiverId, Model model, HttpSession session) {
        
        User sender = (User) session.getAttribute("loggedUser");
        
        if (sender == null) {
            return "redirect:/login";
        }
        
        User receiver = userRepository.findById(receiverId).orElse(null);
        if (receiver == null) {
            return "redirect:/directory";
        }
        
        model.addAttribute("sender", sender);
        model.addAttribute("receiver", receiver);
        model.addAttribute("privateMessage", new PrivateMessage());
        
        return "new-private-message";
    }

    @PostMapping("/messages/new/{receiverId}")
    public String sendPrivateMessage(@PathVariable Long receiverId,
                                   @ModelAttribute PrivateMessage privateMessage,
                                   HttpSession session) {
        
        User sender = (User) session.getAttribute("loggedUser");
        
        if (sender == null) {
            return "redirect:/login";
        }
        
        User receiver = userRepository.findById(receiverId).orElse(null);
        if (receiver == null) {
            return "redirect:/directory";
        }
        
        privateMessage.setSender(sender);
        privateMessage.setReceiver(receiver);
        privateMessage.setSentDate(LocalDateTime.now());
        privateMessage.setIsRead(false);
        
        privateMessageRepository.save(privateMessage);
        
        return "redirect:/directory";
    }

    @GetMapping("/messages/inbox")
    public String inbox(Model model, HttpSession session) {
        
        User user = (User) session.getAttribute("loggedUser");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        // Prepare recent chats list (same logic as AuthController)
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
        
        // Sort by latest message time
        recentChats.sort((a, b) -> b.getLastMessage().getSentDate().compareTo(a.getLastMessage().getSentDate()));
        
        model.addAttribute("recentChats", recentChats);
        model.addAttribute("user", user);
        
        return "inbox-new";
    }

    @GetMapping("/messages/sent")
    public String sentMessages(Model model, HttpSession session) {
        
        User user = (User) session.getAttribute("loggedUser");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        List<PrivateMessage> messages = privateMessageRepository.findBySenderOrderBySentDateDesc(user);
        model.addAttribute("messages", messages);
        model.addAttribute("user", user);
        
        return "sent-messages";
    }

    @PostMapping("/messages/mark-read/{messageId}")
    public String markAsRead(@PathVariable Long messageId, HttpSession session) {
        
        User user = (User) session.getAttribute("loggedUser");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        PrivateMessage message = privateMessageRepository.findById(messageId).orElse(null);
        if (message != null && message.getReceiver().getId().equals(user.getId())) {
            message.setIsRead(true);
            privateMessageRepository.save(message);
        }
        
        return "redirect:/messages/inbox";
    }
}
