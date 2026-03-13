package com.alumni.alumni_portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String role;
    private String location;
    private String applyLink;

    @Column(length = 1000)
    private String description;

    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    public Job() {}

    public Long getId() {
        return id;
    }

    public String getCompany() {
        return company;
    }

    public String getRole() {
        return role;
    }

    public String getLocation() {
        return location;
    }

    public String getApplyLink() {
        return applyLink;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getPublishedDate() {
        return publishedDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setApplyLink(String applyLink) {
        this.applyLink = applyLink;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPublishedDate(LocalDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }
}
