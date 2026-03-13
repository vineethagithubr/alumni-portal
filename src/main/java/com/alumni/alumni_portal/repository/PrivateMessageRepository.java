package com.alumni.alumni_portal.repository;

import com.alumni.alumni_portal.model.PrivateMessage;
import com.alumni.alumni_portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, Long> {

    List<PrivateMessage> findByReceiverOrderBySentDateDesc(User receiver);
    
    List<PrivateMessage> findBySenderOrderBySentDateDesc(User sender);
    
    List<PrivateMessage> findByReceiverAndIsReadOrderBySentDateDesc(User receiver, Boolean isRead);
    
    List<PrivateMessage> findAllByOrderBySentDateDesc();
    
    @Query("SELECT m FROM PrivateMessage m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.sentDate ASC")
    List<PrivateMessage> findConversationBetweenUsers(User user1, User user2);
    
    @Query("SELECT COUNT(m) FROM PrivateMessage m WHERE m.receiver = :user AND m.isRead = false")
    long countUnreadMessages(User user);
}
