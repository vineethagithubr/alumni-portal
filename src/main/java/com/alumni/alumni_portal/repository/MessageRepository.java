package com.alumni.alumni_portal.repository;

import com.alumni.alumni_portal.model.Message;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    @Cacheable(value = "messages")
    List<Message> findTop2ByOrderByPublishedDateDesc();
    
    // Fallback method if publishedDate is null
    @Cacheable(value = "messages")
    List<Message> findTop2ByOrderByIdDesc();
    
    // Methods to fetch all messages
    @Cacheable(value = "messages")
    List<Message> findAllByOrderByPublishedDateDesc();
    
    // Fallback method if publishedDate is null
    @Cacheable(value = "messages")
    List<Message> findAllByOrderByIdDesc();
}
