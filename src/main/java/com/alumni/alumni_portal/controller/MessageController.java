package com.alumni.alumni_portal.controller;

import com.alumni.alumni_portal.model.Message;
import com.alumni.alumni_portal.model.User;
import com.alumni.alumni_portal.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

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
        
        List<Message> messages = messageRepository.findAll();
        model.addAttribute("messages", messages);

        return "messages";
    }
}
