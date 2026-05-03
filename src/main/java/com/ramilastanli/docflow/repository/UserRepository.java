package com.ramilastanli.docflow.repository;

import com.ramilastanli.docflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Login prosesi üçün email ilə axtarış
    Optional<User> findByEmail(String email);
    
    // Əgər username ilə login olacaqsınızsa
    Optional<User> findByUsername(String username);
    
    // Email-in sistemdə olub-olmadığını yoxlamaq üçün (Register zamanı)
    boolean existsByEmail(String email);
}