package com.alumni.alumni_portal.repository;

import com.alumni.alumni_portal.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    
    List<Job> findTop2ByOrderByPublishedDateDesc();
    
    // Fallback method if publishedDate is null
    List<Job> findTop2ByOrderByIdDesc();
    
    // Methods to fetch all jobs
    List<Job> findAllByOrderByPublishedDateDesc();
    
    // Fallback method if publishedDate is null
    List<Job> findAllByOrderByIdDesc();
}
