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
}
