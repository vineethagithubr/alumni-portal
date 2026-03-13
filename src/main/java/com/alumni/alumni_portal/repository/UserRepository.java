package com.alumni.alumni_portal.repository;

import com.alumni.alumni_portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    
    List<User> findByBranch(String branch);
    
    List<User> findByPassoutYear(Integer passoutYear);
    
    List<User> findByNameContainingIgnoreCase(String name);

}
