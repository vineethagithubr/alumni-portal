package com.alumni.alumni_portal.repository;

import com.alumni.alumni_portal.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findTop2ByOrderByPublishedDateDesc();
    
    // Fallback method if publishedDate is null
    List<Event> findTop2ByOrderByIdDesc();
}
