package com.alumni.alumni_portal.controller;

import com.alumni.alumni_portal.model.Message;
import com.alumni.alumni_portal.model.User;
import com.alumni.alumni_portal.repository.MessageRepository;
import com.alumni.alumni_portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserService userService;

    @GetMapping("/messages/new")
    public String newMessageForm(Model model, HttpSession session){
        
        User user = (User) session.getAttribute("loggedUser");
        
        if(user == null){
            return "redirect:/login";
        }
        
        model.addAttribute("message", new Message());
        return "new-message";
    }

    @PostMapping("/messages/new")
    public String createMessage(@ModelAttribute Message message, HttpSession session){
        
        User user = (User) session.getAttribute("loggedUser");
        
        if(user == null){
            return "redirect:/login";
        }
        
        message.setName(user.getName());
        message.setPublishedDate(LocalDateTime.now());
        
        messageRepository.save(message);
        
        return "redirect:/dashboard";
    }

    @GetMapping("/messages")
    public String viewMessages(Model model, HttpSession session){

        User user = (User) session.getAttribute("loggedUser");

        if(user == null){
            return "redirect:/login";
        }
        
        // Check if user is admin
        boolean isAdmin = userService.isAdmin(user);
        model.addAttribute("isAdmin", isAdmin);
        
        List<Message> messages = messageRepository.findAll();
        model.addAttribute("messages", messages);

        return "messages";
    }
    
    @PostMapping("/messages/delete/{id}")
    public String deleteMessage(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedUser");
        
        if(user == null){
            return "redirect:/login";
        }
        
        // Check if user is admin
        if (!userService.isAdmin(user)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/messages";
        }
        
        messageRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Message deleted successfully.");
        
        return "redirect:/messages";
    }
}
