package com.alumni.alumni_portal.dto;

import com.alumni.alumni_portal.model.PrivateMessage;
import com.alumni.alumni_portal.model.User;

public class ChatInfo {
    
    private User user;
    private PrivateMessage lastMessage;
    private long unreadCount;
    
    public ChatInfo() {}
    
    public ChatInfo(User user, PrivateMessage lastMessage, long unreadCount) {
        this.user = user;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public PrivateMessage getLastMessage() {
        return lastMessage;
    }
    
    public void setLastMessage(PrivateMessage lastMessage) {
        this.lastMessage = lastMessage;
    }
    
    public long getUnreadCount() {
        return unreadCount;
    }
    
    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
