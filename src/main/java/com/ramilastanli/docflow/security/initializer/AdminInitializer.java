package com.ramilastanli.docflow.security.initializer;

import com.ramilastanli.docflow.entity.Role;
import com.ramilastanli.docflow.entity.User;
import com.ramilastanli.docflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Sistemdə adminin olub-olmadığını yoxlayırıq
        String adminEmail = "admin@docflow.com";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setUsername("superadmin");
            admin.setEmail(adminEmail);
            // Şifrəni mütləq encode edib saxlamalıyıq
            admin.setPassword(passwordEncoder.encode("admin123"));
            // Rolu birbaşa ADMIN olaraq set edirik
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);
            
            System.out.println("--------------------------------------");
            System.out.println("SİSTEM: İlkin Admin hesabı yaradıldı!");
            System.out.println("Email: admin@docflow.com");
            System.out.println("Şifrə: admin123");
            System.out.println("--------------------------------------");
        } else {
            System.out.println("SİSTEM: Admin hesabı artıq mövcuddur.");
        }
    }
}