package com.alumni.alumni_portal.controller;

import com.alumni.alumni_portal.model.User;
import com.alumni.alumni_portal.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public String viewProfile(Model model, HttpSession session){

        User user = (User) session.getAttribute("loggedUser");

        if(user == null){
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);

        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, HttpSession session){

        User user = (User) session.getAttribute("loggedUser");

        if(user == null){
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);

        return "edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String branch,
            @RequestParam Integer passoutYear,
            HttpSession session,
            RedirectAttributes redirectAttributes){

        User user = (User) session.getAttribute("loggedUser");

        if(user == null){
            return "redirect:/login";
        }

        try {
            // Update user details (only existing fields)
            user.setName(name);
            user.setEmail(email);
            user.setBranch(branch);
            user.setPassoutYear(passoutYear);
            
            // Save updated user
            userRepository.save(user);
            
            // Update session
            session.setAttribute("loggedUser", user);
            
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    @GetMapping("/directory")
    public String viewDirectory(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) Integer passoutYear,
            @RequestParam(required = false) String name,
            Model model, HttpSession session){

        User user = (User) session.getAttribute("loggedUser");

        if(user == null){
            return "redirect:/login";
        }

        List<User> users;
        
        try {
            if (name != null && !name.isEmpty()) {
                System.out.println("Searching by name: " + name);
                users = userRepository.findByNameContainingIgnoreCase(name);
            } else if (branch != null && !branch.isEmpty()) {
                System.out.println("Filtering by branch: " + branch);
                users = userRepository.findByBranch(branch);
            } else if (passoutYear != null) {
                System.out.println("Filtering by year: " + passoutYear);
                users = userRepository.findByPassoutYear(passoutYear);
            } else {
                System.out.println("Getting all users");
                users = userRepository.findAll();
            }
            
            System.out.println("Found " + users.size() + " users");
            
        } catch (Exception e) {
            System.out.println("Error in directory: " + e.getMessage());
            users = userRepository.findAll();
        }
        
        model.addAttribute("users", users);
        model.addAttribute("selectedBranch", branch);
        model.addAttribute("selectedYear", passoutYear);
        model.addAttribute("searchName", name);

        return "directory-test";
    }
}
