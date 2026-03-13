package com.alumni.alumni_portal.controller;

import com.alumni.alumni_portal.model.Job;
import com.alumni.alumni_portal.model.User;
import com.alumni.alumni_portal.repository.JobRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @GetMapping("/jobs/new")
    public String newJobForm(Model model, HttpSession session){
        
        User user = (User) session.getAttribute("loggedUser");
        
        if(user == null){
            return "redirect:/login";
        }
        
        model.addAttribute("job", new Job());
        return "new-job";
    }

    @PostMapping("/jobs/new")
    public String createJob(@ModelAttribute Job job, HttpSession session){
        
        User user = (User) session.getAttribute("loggedUser");
        
        if(user == null){
            return "redirect:/login";
        }
        
        job.setPublishedDate(LocalDateTime.now());
        
        jobRepository.save(job);
        
        return "redirect:/dashboard";
    }

    @GetMapping("/jobs")
    public String viewJobs(Model model, HttpSession session){
        User user = (User) session.getAttribute("loggedUser");

        if(user == null){
            return "redirect:/login";
        }
        
        List<Job> jobs = jobRepository.findAll();
        model.addAttribute("jobs", jobs);

        return "jobs";
    }
}
