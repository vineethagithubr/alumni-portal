package com.alumni.alumni_portal.service;

import com.alumni.alumni_portal.model.User;
import com.alumni.alumni_portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(User user){
        userRepository.save(user);
    }

    public User login(String email, String password){
        User user = userRepository.findByEmail(email);

        if(user != null && user.getPassword().equals(password)){
            return user;
        }

        return null;
    }
}