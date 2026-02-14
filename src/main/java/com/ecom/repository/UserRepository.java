package com.ecom.repository;

import com.ecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
 // Matches the "xp" field in your User model
    List<User> findTop10ByOrderByXpDesc();
 

    // ----------------- New: Search users -----------------
    Page<User> findByUsernameContainingIgnoreCase(@Param("username") String username, Pageable pageable);
}
