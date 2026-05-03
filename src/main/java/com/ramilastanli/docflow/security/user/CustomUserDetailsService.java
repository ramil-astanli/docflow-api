package com.ramilastanli.docflow.security.user;

import com.ramilastanli.docflow.entity.User;
import com.ramilastanli.docflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public final UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("İstifadəçi tapılmadı: " + email));

        // 1. Rolun adını hazırlayırıq (Spring Security üçün)
        String roleName = user.getRole().name();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        // 2. CustomUserDetails konstruktoruna mütləq User obyektindən gələn Role-u da ötürürük
        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(), // <--- Bura əlavə etdiyimiz Role Enum-dur
                List.of(authority)
        );
    }
}