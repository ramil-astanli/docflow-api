package com.ramilastanli.docflow.config;

import com.ramilastanli.docflow.core.repository.DocumentConfigRepository;
import com.ramilastanli.docflow.core.entity.ConfigApprover;
import com.ramilastanli.docflow.core.entity.DocumentConfig;
import com.ramilastanli.docflow.core.entity.enums.Role;
import com.ramilastanli.docflow.core.entity.User;
import com.ramilastanli.docflow.core.repository.ConfigApproverRepository;
import com.ramilastanli.docflow.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DocumentConfigRepository configRepository;
    private final ConfigApproverRepository approverRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) {
            createInitialUsers();
        }

        if (configRepository.count() == 0) {
            createDocumentConfigs();
        }
    }

    private void createInitialUsers() {
        List<User> users = List.of(
                new User(null, "manager1", passwordEncoder.encode("Pass12345"), "manager@co.com", Role.APPROVER, "Finance", "1"),
                new User(null, "director1", passwordEncoder.encode("Pass12345"), "director@co.com", Role.APPROVER, "Finance", "2"),
                new User(null, "cfo1", passwordEncoder.encode("Pass12345"), "cfo@co.com", Role.APPROVER, "Finance", "3"),
                new User(null, "hr_lead", passwordEncoder.encode("Pass12345"), "hr_lead@co.com", Role.APPROVER, "HR", "1")
        );
        userRepository.saveAllAndFlush(users);
        System.out.println(">> Təsdiqçi istifadəçilər yaradıldı.");
    }

    private void createDocumentConfigs() {
        DocumentConfig financial = new DocumentConfig("FINANCIAL", 3, "Maliyyə sənədləri", null);
        configRepository.save(financial);

        approverRepository.saveAll(List.of(
                new ConfigApprover(null, financial, "manager@co.com", 1),
                new ConfigApprover(null, financial, "director@co.com", 2),
                new ConfigApprover(null, financial, "cfo@co.com", 3)
        ));

        DocumentConfig hr = new DocumentConfig("HR", 1, "HR sənədləri", null);
        configRepository.save(hr);
        approverRepository.save(new ConfigApprover(null, hr, "hr_lead@co.com", 1));

        System.out.println(">> Sənəd konfiqurasiyaları və təyinatlar tamamlandı.");
    }
}