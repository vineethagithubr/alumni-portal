package com.alumni.alumni_portal.controller;

import com.alumni.alumni_portal.model.Event;
import com.alumni.alumni_portal.model.User;
import com.alumni.alumni_portal.repository.EventRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/events/new")
    public String newEventForm(Model model, HttpSession session){
        
        User user = (User) session.getAttribute("loggedUser");
        
        if(user == null){
            return "redirect:/login";
        }
        
        model.addAttribute("event", new Event());
        return "new-event";
    }

    @PostMapping("/events/new")
    public String createEvent(@ModelAttribute Event event, HttpSession session){
        
        User user = (User) session.getAttribute("loggedUser");
        
        if(user == null){
            return "redirect:/login";
        }
        
        event.setPublishedDate(LocalDateTime.now());
        
        eventRepository.save(event);
        
        return "redirect:/dashboard";
    }

    @GetMapping("/events")
    public String viewEvents(Model model, HttpSession session){

        User user = (User) session.getAttribute("loggedUser");

        if(user == null){
            return "redirect:/login";
        }
        
        List<Event> events = eventRepository.findAll();
        model.addAttribute("events", events);

        return "events";
    }
}
