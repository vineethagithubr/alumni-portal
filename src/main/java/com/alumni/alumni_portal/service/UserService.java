package com.alumni.alumni_portal.service;

import com.alumni.alumni_portal.model.User;
import com.alumni.alumni_portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(User user){
        // First user becomes admin, others are regular users
        if (userRepository.count() == 0) {
            user.setRole(User.Role.ADMIN);
        } else {
            user.setRole(User.Role.USER);
        }
        userRepository.save(user);
    }

    public User login(String email, String password){
        List<User> users = userRepository.findByEmail(email);
        
        if(users != null && !users.isEmpty()){
            for(User user : users){
                if(user.getPassword().equals(password)){
                    return user;
                }
            }
        }
        
        return null;
    }

    public boolean isAdmin(User user) {
        return user != null && user.getRole() == User.Role.ADMIN;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public void updateUserRole(Long userId, User.Role role) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setRole(role);
            userRepository.save(user);
        }
    }
    
    public boolean verifyPassword(User user, String password) {
        // Simple password verification (in production, use BCrypt)
        return user.getPassword().equals(password);
    }
    
    public void updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}
